package com.ecnu.tripmap.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class SwaggerController {

    @GetMapping("/api")
    public String index() {
        return "redirect:swagger-ui.html";
    }

}
