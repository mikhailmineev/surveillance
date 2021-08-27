package ru.mm.surv.capture.service

import java.io.File
import java.util.*

interface FfmpegStream {
    fun start()
    fun stop()
    val isActive: Boolean
    val streamNames: Collection<String>
    fun getThumb(stream: String): File?
}
