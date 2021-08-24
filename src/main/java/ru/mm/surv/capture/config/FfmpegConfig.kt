package ru.mm.surv.capture.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "ffmpeg")
class FfmpegConfig(
    val recorder: Map<String, CameraConfig>
)

