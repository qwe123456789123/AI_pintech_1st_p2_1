package org.koreait.mypage.controllers;

// 필요한 클래스 및 애너테이션 임포트
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.member.MemberInfo;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.mypage.validators.ProfileValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MypageController는 마이페이지와 관련된 기능을 제공하는 컨트롤러입니다.
 * - 회원 정보 조회, 수정
 * - 프로필 페이지 처리
 * - 회원 정보 새로고침
 */
@Controller
@ApplyErrorPage // 예외 발생 시 전용 에러 페이지 처리
@RequestMapping("/mypage")
@RequiredArgsConstructor
@SessionAttributes("profile") // "profile" 속성을 세션에 유지
public class MypageController {

    private final Utils utils; // 유틸리티 클래스
    private final MemberUtil memberUtil; // 로그인 사용자 정보를 다루는 유틸리티
    private final ModelMapper modelMapper; // 객체 매핑 도구
    private final MemberUpdateService updateService; // 회원 정보 업데이트 서비스
    private final ProfileValidator profileValidator; // 프로필 유효성 검증
    private final MemberInfoService infoService; // 회원 정보 조회 서비스

    /**
     * 세션에서 회원 정보를 가져와 ModelAttribute로 추가합니다.
     *
     * @return 현재 로그인된 회원 정보
     */
    @ModelAttribute("profile")
    public Member getMember() {
        return memberUtil.getMember();
    }

    /**
     * 마이페이지 CSS 파일을 모델에 추가합니다.
     *
     * @return CSS 파일 목록
     */
    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("mypage/style");
    }

    /**
     * 마이페이지 메인 화면
     *
     * @param model 모델 객체
     * @return 마이페이지 메인 템플릿 경로
     */
    @GetMapping
    public String index(Model model) {
        commonProcess("main", model);
        return utils.tpl("mypage/index");
    }

    /**
     * 프로필 페이지 진입
     *
     * @param model 모델 객체
     * @return 프로필 템플릿 경로
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        commonProcess("profile", model);

        // 현재 회원 정보를 RequestProfile 객체로 매핑
        Member member = memberUtil.getMember();
        RequestProfile form = modelMapper.map(member, RequestProfile.class);

        // 선택적 약관 데이터 처리
        String optionalTerms = member.getOptionalTerms();
        if (StringUtils.hasText(optionalTerms)) {
            form.setOptionalTerms(Arrays.stream(optionalTerms.split("\\|\\|")).toList());
        }

        model.addAttribute("requestProfile", form);

        return utils.tpl("mypage/profile");
    }

    /**
     * 프로필 수정 처리
     *
     * @param form   수정할 회원 정보 폼 객체
     * @param errors 유효성 검사 에러 객체
     * @param model  모델 객체
     * @return 수정 완료 후 마이페이지로 리다이렉트
     */
    @PatchMapping("/profile")
    public String updateProfile(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("profile", model);

        // 프로필 유효성 검사
        profileValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/profile"); // 유효성 에러 발생 시 다시 프로필 페이지로 이동
        }

        // 회원 정보 업데이트
        updateService.process(form);

        // 세션의 프로필 정보 업데이트
        model.addAttribute("profile", memberUtil.getMember());

        return "redirect:/mypage";
    }

    /**
     * 회원 정보 새로고침
     * - 로그인된 회원의 정보를 새로 조회하여 업데이트합니다.
     *
     * @param principal 현재 인증된 사용자
     * @param model     모델 객체
     */
    @ResponseBody
    @GetMapping("/refresh")
    public void refresh(Principal principal, Model model) {
        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(principal.getName());
        memberUtil.setMember(memberInfo.getMember());

        model.addAttribute("profile", memberInfo.getMember());
    }

    /**
     * 공통 프로세스 처리
     * - 페이지에 따라 공통 스크립트, 페이지 타이틀 등을 설정합니다.
     *
     * @param mode  페이지 모드 ("main" 또는 "profile")
     * @param model 모델 객체
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "main";
        String pageTitle = utils.getMessage("마이페이지");

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("profile")) { // 프로필 수정 페이지
            addCommonScript.add("fileManager");
            addCommonScript.add("address");
            addScript.add("mypage/profile");
            pageTitle = utils.getMessage("회원정보_수정");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
    }
}
