package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.mm.surv.capture.service.FfmpegStream;

@RestController
@RequestMapping("stream/control")
public class StreamController {

    private final FfmpegStream ffmpegStream;

    @Autowired
    public StreamController(FfmpegStream ffmpegStream) {
        this.ffmpegStream = ffmpegStream;
    }

    @GetMapping("start")
    public RedirectView startUrl(@RequestHeader("Referer") String referer) {
        ffmpegStream.start();
        return new RedirectView(referer);
    }

    @PostMapping("start")
    public void start() {
        ffmpegStream.start();
    }

    @GetMapping("stop")
    public RedirectView stopUrl(@RequestHeader("Referer") String referer)  {
        ffmpegStream.stop();
        return new RedirectView(referer);
    }

    @PostMapping("stop")
    public void stop()  {
        ffmpegStream.stop();
    }
}
