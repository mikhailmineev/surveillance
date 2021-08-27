package ru.mm.surv.capture.repository.impl

import org.springframework.stereotype.Repository
import ru.mm.surv.capture.config.FfmpegConfig
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.capture.config.CameraConfig

@Repository
class PropertiesWebcamRepository(private val config: FfmpegConfig) : WebcamRepository {

    override fun all(): Collection<CameraConfig> {
        return config.recorder.values
    }
}
