package ru.mm.surv.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.mm.surv.capture.WebcamDiscovery
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.capture.service.FfmpegStream
import ru.mm.surv.dto.SystemConfig
import ru.mm.surv.dto.SystemInfo

@RestController
class SystemController(
    private val ffmpegStream: FfmpegStream,
    private val webcamRepository: WebcamRepository,
    private val webcamDiscovery: WebcamDiscovery
) {

    @GetMapping("system")
    fun systemInfo(): SystemInfo {
        return SystemInfo(ffmpegStream.status())
    }

    @GetMapping("system/config")
    fun systemConfig(): SystemConfig {
        val recorders = webcamRepository.all()
        val inputSources = webcamDiscovery.getInputSources()
        return SystemConfig(recorders, inputSources)
    }
}
