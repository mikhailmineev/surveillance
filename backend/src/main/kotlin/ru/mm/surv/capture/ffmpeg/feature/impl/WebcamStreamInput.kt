package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.Platform
import ru.mm.surv.capture.ffmpeg.feature.Feature

class WebcamStreamInput(
    private val captureConfig: CameraConfig,
    private val platform: Platform
    ): Feature {

    override fun args(): Array<String> {
        return arrayOf(
            "-f", platform.osCaptureFunction,
            "-s", captureConfig.inputResolution,
            "-framerate", captureConfig.inputFramerate,
            "-i", platform.getSelector(captureConfig),
            "-r", "16",
            "-async", "1",
            "-vsync", "1",
            "-g", "16",
            "-vcodec", "libvpx",
            "-vb", "448k",
            "-acodec", "libvorbis",
            "-ab", "64k"
        )
    }

}
