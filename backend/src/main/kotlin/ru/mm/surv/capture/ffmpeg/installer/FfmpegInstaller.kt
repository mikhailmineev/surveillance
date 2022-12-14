package ru.mm.surv.capture.ffmpeg.installer

import java.nio.file.Path

fun interface FfmpegInstaller {
    fun path(): Path
}
