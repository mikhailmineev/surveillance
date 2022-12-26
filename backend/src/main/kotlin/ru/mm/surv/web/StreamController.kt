package ru.mm.surv.web

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import ru.mm.surv.capture.service.FfmpegStream
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import ru.mm.surv.dto.StreamInfo

@RestController
@RequestMapping("stream")
class StreamController @Autowired constructor(private val ffmpegStream: FfmpegStream) {

    @GetMapping
    fun streams(): StreamInfo {
        return StreamInfo(ffmpegStream.status(), ffmpegStream.streamNames())
    }

    @PostMapping("control/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun start() {
        ffmpegStream.start()
    }

    @PostMapping("control/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun stop() {
        ffmpegStream.stop()
    }
}
