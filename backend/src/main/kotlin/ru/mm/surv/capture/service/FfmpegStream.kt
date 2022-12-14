package ru.mm.surv.capture.service

import ru.mm.surv.dto.StreamStatus
import java.io.File

interface FfmpegStream {
    fun start()
    fun stop()
    fun status(): StreamStatus
    fun streamNames(): Collection<String>
    fun getThumb(stream: String): File?
}
