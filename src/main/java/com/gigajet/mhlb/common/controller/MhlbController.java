package com.gigajet.mhlb.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MhlbController {

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
