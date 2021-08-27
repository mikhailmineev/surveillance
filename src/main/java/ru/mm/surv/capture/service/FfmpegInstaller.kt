package ru.mm.surv.capture.service

import java.nio.file.Path

interface FfmpegInstaller {
    fun path(): Path
}
