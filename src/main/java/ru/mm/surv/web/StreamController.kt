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
@RequestMapping("stream/control")
class StreamController @Autowired constructor(private val ffmpegStream: FfmpegStream) {

    @GetMapping("start")
    fun startUrl(@RequestHeader("Referer") referer: String): RedirectView {
        ffmpegStream.start()
        return RedirectView(referer)
    }

    @PostMapping("start")
    fun start() {
        ffmpegStream.start()
    }

    @GetMapping("stop")
    fun stopUrl(@RequestHeader("Referer") referer: String): RedirectView {
        ffmpegStream.stop()
        return RedirectView(referer)
    }

    @PostMapping("stop")
    fun stop() {
        ffmpegStream.stop()
    }
}
