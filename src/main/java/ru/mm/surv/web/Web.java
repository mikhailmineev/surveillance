package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.config.FfmpegConfig;

import java.util.Set;

@Controller
public class Web {

    private final FfmpegConfig ffmpegConfig;

    @Autowired
    public Web(FfmpegConfig ffmpegConfig) {
        this.ffmpegConfig = ffmpegConfig;
    }

    @GetMapping
    public String mainPage(Model model) {
        Set<String> recorders = ffmpegConfig.getRecorder().keySet();
        model.addAttribute("recorders", recorders);
        return "video.html";
    }
}
