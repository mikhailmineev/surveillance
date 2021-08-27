package ru.mm.surv.capture.repository

import ru.mm.surv.capture.config.CameraConfig

interface WebcamRepository {
    fun all(): Collection<CameraConfig>
}
