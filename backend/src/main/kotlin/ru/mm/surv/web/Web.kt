package ru.mm.surv.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import ru.mm.surv.capture.service.FfmpegStream
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.capture.WebcamDiscovery
import org.springframework.web.bind.annotation.GetMapping

@Controller
class Web(
        private val ffmpegStream: FfmpegStream,
        private val webcamRepository: WebcamRepository,
        private val webcamDiscovery: WebcamDiscovery) {

    @GetMapping("configure")
    fun configPage(model: Model): String {
        val recorders = webcamRepository.all()
        val inputSources = webcamDiscovery.getInputSources()
        model["recorders"] = recorders
        model["inputSources"] = inputSources
        model["streamActive"] = ffmpegStream.isActive()
        return "configure.html"
    }

    @GetMapping("actuatorui")
    fun actuatorPage(model: Model): String {
        return "actuator.html"
    }
}
