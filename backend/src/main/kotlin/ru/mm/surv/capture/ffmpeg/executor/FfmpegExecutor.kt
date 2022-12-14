package ru.mm.surv.capture.ffmpeg.executor

import ru.mm.surv.dto.StreamStatus
import java.io.File

interface FfmpegExecutor {
    fun getName(): String
    fun getThumb(): File?
    fun getStatus(): StreamStatus
    fun stop()
}
