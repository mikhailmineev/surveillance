package ru.mm.surv.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.mm.surv.capture.WebcamDiscovery
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.dto.SystemConfig

@RestController
class ConfigController(
    private val webcamRepository: WebcamRepository,
    private val webcamDiscovery: WebcamDiscovery
) {

    @GetMapping("config")
    fun config(): SystemConfig {
        val recorders = webcamRepository.all()
        val inputSources = webcamDiscovery.getInputSources()
        return SystemConfig(recorders, inputSources)
    }
}
