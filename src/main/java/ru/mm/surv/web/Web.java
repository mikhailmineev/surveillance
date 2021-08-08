package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.WebcamDiscovery;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.config.InputSource;
import ru.mm.surv.capture.repository.WebcamRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class Web {

    private final WebcamRepository webcamRepository;
    private final WebcamDiscovery webcamDiscovery;

    @Autowired
    public Web(WebcamRepository webcamRepository, WebcamDiscovery webcamDiscovery) {
        this.webcamRepository = webcamRepository;
        this.webcamDiscovery = webcamDiscovery;
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
        List<InputSource> inputSources = webcamDiscovery.getInputSources();
        model.addAttribute("recorders", recorders);
        model.addAttribute("inputSources", inputSources);
        return "configure.html";
    }
}
