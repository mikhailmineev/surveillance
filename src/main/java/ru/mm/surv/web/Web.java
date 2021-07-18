package ru.mm.surv.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class Web {

    @GetMapping
    public String mainPage() {
        return "video.html";
    }
}
