package ru.mm.surv.capture.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.nio.file.Path

@ConstructorBinding
@ConfigurationProperties(prefix = "ffmpeg.folder")
data class FolderConfig(
    val streamThumb: Path,
    val hls: Path,
    val mp4: Path,
    val mp4Thumb: Path
)
