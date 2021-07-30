package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.repository.WebcamRepository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class Web {

    private final WebcamRepository webcamRepository;

    @Autowired
    public Web(WebcamRepository webcamRepository) {
        this.webcamRepository = webcamRepository;
    }

    @GetMapping
    public String mainPage(Model model) {
        Set<String> recorders = webcamRepository.getAll()
                .stream()
                .map(CameraConfig::getName)
                .collect(Collectors.toSet());
        model.addAttribute("recorders", recorders);
        return "video.html";
    }

    @GetMapping("configure")
    public String configPage(Model model) {
        Collection<CameraConfig> recorders = webcamRepository.getAll();
        model.addAttribute("recorders", recorders);
        return "configure.html";
    }
}
