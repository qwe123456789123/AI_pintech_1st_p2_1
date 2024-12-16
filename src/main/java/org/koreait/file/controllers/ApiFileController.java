package org.koreait.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.*;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

/**
 * ApiFileController는 파일 업로드, 조회, 다운로드, 삭제 등의 파일 처리 관련 API를 제공합니다.
 * - Swagger를 통해 API 문서를 자동 생성하며, 요청/응답 명세를 포함합니다.
 */
@Tag(name = "파일 API", description = "파일 업로드, 조회, 다운로드, 삭제 기능 제공합니다.")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor // `final` 필드를 주입받아 생성자를 자동 생성
public class ApiFileController {

    private final Utils utils; // 유틸리티 클래스
    private final FileUploadService uploadService; // 파일 업로드 서비스
    private final FileDownloadService downloadService; // 파일 다운로드 서비스
    private final FileInfoService infoService; // 파일 정보 조회 서비스
    private final FileDeleteService deleteService; // 파일 삭제 서비스
    private final FileDoneService doneService; // 파일 완료 처리 서비스
    private final ThumbnailService thumbnailService; // 썸네일 생성 서비스

    /**
     * 파일 업로드 API
     * - 업로드된 파일은 서버에 저장되며, 필요 시 기존 파일을 삭제하고 새로 추가됩니다.
     *
     * @param files 업로드할 파일 배열
     * @param form 파일 업로드 요청 데이터
     * @param errors 검증 에러 객체
     * @return 업로드된 파일 목록을 포함한 JSON 데이터
     */
    @Operation(summary = "파일 업로드 처리")
    @ApiResponse(responseCode = "201", description = "파일 업로드 성공 시 업로드된 파일 목록 반환")
    @Parameters({
            @Parameter(name = "gid", description = "파일 그룹 ID", required = true),
            @Parameter(name = "location", description = "파일 그룹 내 위치 코드"),
            @Parameter(name = "file", description = "업로드할 파일", required = true)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public JSONData upload(@RequestPart("file") MultipartFile[] files, @Valid RequestUpload form, Errors errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors)); // 유효성 검증 실패 시 예외 처리
        }

        form.setFiles(files);

        // 단일 파일 업로드: 기존 파일 삭제 후 업로드
        if (form.isSingle()) {
            deleteService.deletes(form.getGid(), form.getLocation());
        }

        List<FileInfo> uploadedFiles = uploadService.upload(form);

        // 업로드 완료 즉시 완료 처리
        if (form.isDone()) {
            doneService.process(form.getGid(), form.getLocation());
        }

        JSONData data = new JSONData(uploadedFiles);
        data.setStatus(HttpStatus.CREATED);

        return data;
    }

    /**
     * 파일 다운로드 API
     * - 파일의 고유 ID(seq)를 기반으로 다운로드 처리.
     *
     * @param seq 파일 고유 ID
     */
    @GetMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq) {
        downloadService.process(seq);
    }

    /**
     * 파일 정보 조회 API
     * - 파일의 고유 ID(seq)를 기반으로 단일 파일 정보 반환.
     *
     * @param seq 파일 고유 ID
     * @return 파일 정보 JSON 데이터
     */
    @GetMapping("/info/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) {
        FileInfo item = infoService.get(seq);

        return new JSONData(item);
    }

    /**
     * 파일 목록 조회 API
     * - 파일 그룹 ID(gid)와 위치(location), 상태(status)를 기반으로 파일 목록 조회.
     *
     * @param gid 파일 그룹 ID
     * @param location 파일 위치 (선택적)
     * @param status 파일 상태 (기본값: DONE)
     * @return 파일 목록 JSON 데이터
     */
    @GetMapping(path = {"/list/{gid}", "/list/{gid}/{location}"})
    public JSONData list(@PathVariable("gid") String gid,
                         @PathVariable(name = "location", required = false) String location,
                         @RequestParam(name = "status", defaultValue = "DONE") FileStatus status) {

        List<FileInfo> items = infoService.getList(gid, location, status);

        return new JSONData(items);
    }

    /**
     * 파일 단일 삭제 API
     * - 파일의 고유 ID(seq)를 기반으로 삭제 처리.
     *
     * @param seq 파일 고유 ID
     * @return 삭제된 파일 정보 JSON 데이터
     */
    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {
        FileInfo item = deleteService.delete(seq);

        return new JSONData(item);
    }

    /**
     * 파일 다중 삭제 API
     * - 파일 그룹 ID(gid)와 위치(location)를 기반으로 다중 파일 삭제 처리.
     *
     * @param gid 파일 그룹 ID
     * @param location 파일 위치 (선택적)
     * @return 삭제된 파일 목록 JSON 데이터
     */
    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public JSONData deletes(@PathVariable("gid") String gid,
                            @PathVariable(name = "location", required = false) String location) {

        List<FileInfo> items = deleteService.deletes(gid, location);

        return new JSONData(items);
    }

    /**
     * 썸네일 생성 및 반환 API
     * - 요청 정보를 기반으로 썸네일 이미지를 생성하여 반환.
     *
     * @param form 썸네일 요청 정보
     * @param response HTTP 응답 객체
     */
    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response) {
        String path = thumbnailService.create(form);
        if (!StringUtils.hasText(path)) {
            return; // 경로가 없으면 종료
        }

        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            String contentType = Files.probeContentType(file.toPath());
            response.setContentType(contentType);

            OutputStream out = response.getOutputStream();
            out.write(bis.readAllBytes());

        } catch (IOException e) {
            e.printStackTrace(); // 예외 로그 출력
        }
    }
}
