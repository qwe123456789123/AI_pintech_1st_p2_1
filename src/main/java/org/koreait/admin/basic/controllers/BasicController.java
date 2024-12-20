package org.koreait.admin.basic.controllers;


import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ApplyErrorPage
@RequiredArgsConstructor
@RequestMapping("/admin/basic")
public class BasicController {

    @GetMapping({"/", "siteConfig"})
    public String siteConfig(){

        return "admin/basic/siteConfig";
    }
}
