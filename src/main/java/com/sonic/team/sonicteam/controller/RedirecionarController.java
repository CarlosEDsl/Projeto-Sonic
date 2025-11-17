package com.sonic.team.sonicteam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirecionarController {

    @GetMapping("/")
    public String redirecionarParaSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
