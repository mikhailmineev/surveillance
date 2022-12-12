package ru.mm.surv.dto

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.InputSource

data class SystemConfig(
    val recorders: Collection<CameraConfig>,
    val inputSources: Collection<InputSource>
)
