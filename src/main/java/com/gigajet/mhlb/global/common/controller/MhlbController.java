package com.gigajet.mhlb.global.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MhlbController {

    @GetMapping("/")
    public ModelAndView index1() {
        return new ModelAndView("index");
    }

    @PostMapping("/")
    public ModelAndView index2() {
        return new ModelAndView("index");
    }
}
