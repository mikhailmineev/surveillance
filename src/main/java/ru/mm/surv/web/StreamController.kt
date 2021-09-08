package ru.mm.surv.web

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import ru.mm.surv.capture.service.FfmpegStream
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.bind.annotation.PostMapping

@RestController
@RequestMapping("stream")
class StreamController @Autowired constructor(private val ffmpegStream: FfmpegStream) {

    @GetMapping
    fun streams(): Collection<String> {
        return ffmpegStream.streamNames()
    }

    @GetMapping("control/start")
    fun startUrl(@RequestHeader("Referer") referer: String): RedirectView {
        ffmpegStream.start()
        return RedirectView(referer)
    }

    @PostMapping("control/start")
    fun start() {
        ffmpegStream.start()
    }

    @GetMapping("control/stop")
    fun stopUrl(@RequestHeader("Referer") referer: String): RedirectView {
        ffmpegStream.stop()
        return RedirectView(referer)
    }

    @PostMapping("control/stop")
    fun stop() {
        ffmpegStream.stop()
    }
}
