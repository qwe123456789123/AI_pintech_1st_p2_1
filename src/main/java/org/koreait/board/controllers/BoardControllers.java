package org.koreait.board.controllers;


import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardControllers {
    private final Utils utils;
    private final MemberUtil memberUtil;
    private final ModelMapper modelMapper;


    @ModelAttribute("board")
    public Member getMember() {
        return memberUtil.getMember();
    }
    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("board/style");
    }

    @GetMapping
    public String index(Model model) {

        return utils.tpl("board/index");
    }
}
