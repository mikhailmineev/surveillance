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
import ru.mm.surv.capture.service.RecordService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Controller
public class Web {

    private final FfmpegStream ffmpegStream;
    private final WebcamRepository webcamRepository;
    private final WebcamDiscovery webcamDiscovery;
    private final RecordService recordService;

    @Autowired
    public Web(FfmpegStream ffmpegStream, WebcamRepository webcamRepository, WebcamDiscovery webcamDiscovery, RecordService recordService) {
        this.ffmpegStream = ffmpegStream;
        this.webcamRepository = webcamRepository;
        this.webcamDiscovery = webcamDiscovery;
        this.recordService = recordService;
    }

    @GetMapping
    public String mainPage(Model model) throws IOException {
        Collection<String> streams = ffmpegStream.getStreamNames();
        model.addAttribute("streams", streams);
        Collection<String> records = recordService.getRecords();
        model.addAttribute("records", records);
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
