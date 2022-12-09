package ru.mm.surv.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.mm.surv.capture.service.FfmpegStream
import ru.mm.surv.dto.SystemInfo

@RestController
class SystemController(private val ffmpegStream: FfmpegStream) {

    @GetMapping("system")
    fun systemInfo(): SystemInfo {
        return SystemInfo(ffmpegStream.isActive())
    }
}
