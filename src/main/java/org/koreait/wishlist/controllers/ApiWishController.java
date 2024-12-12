package org.koreait.wishlist.controllers;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class ApiWishController {

    private final HttpServletRequest request;
    private final WishService service;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping({"/add","/remove"})
    public void process(@RequestParam("seq")Long seq, @RequestParam("type") WishType type){
        String mode = request.getRequestURI().contains("/remove") ? "remove" : "add";

        service.process(mode, seq, type);

    }
}

