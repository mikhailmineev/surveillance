package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.WebcamDiscovery;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.config.InputSource;
import ru.mm.surv.capture.repository.WebcamRepository;
import ru.mm.surv.capture.service.FfmpegStream;

import java.util.Collection;
import java.util.List;

@Controller
public class Web {

    private final FfmpegStream ffmpegStream;
    private final WebcamRepository webcamRepository;
    private final WebcamDiscovery webcamDiscovery;

    @Autowired
    public Web(FfmpegStream ffmpegStream, WebcamRepository webcamRepository, WebcamDiscovery webcamDiscovery) {
        this.ffmpegStream = ffmpegStream;
        this.webcamRepository = webcamRepository;
        this.webcamDiscovery = webcamDiscovery;
    }

    @GetMapping
    public String mainPage(Model model) {
        Collection<String> recorders = ffmpegStream.getStreamNames();
        model.addAttribute("recorders", recorders);
        model.addAttribute("streamActive", ffmpegStream.isActive());
        return "video.html";
    }

    @GetMapping("configure")
    public String configPage(Model model) {
        Collection<CameraConfig> recorders = webcamRepository.getAll();
        List<InputSource> inputSources = webcamDiscovery.getInputSources();
        model.addAttribute("recorders", recorders);
        model.addAttribute("inputSources", inputSources);
        model.addAttribute("streamActive", ffmpegStream.isActive());
        return "configure.html";
    }
}
