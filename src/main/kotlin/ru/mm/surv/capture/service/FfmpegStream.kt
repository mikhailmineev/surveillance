package ru.mm.surv.capture.service

import java.io.File

interface FfmpegStream {
    fun start()
    fun stop()
    fun isActive(): Boolean
    fun streamNames(): Collection<String>
    fun getThumb(stream: String): File?
}
