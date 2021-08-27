package ru.mm.surv.capture.config

import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
data class CameraConfig(
    val name: String,
    val video: String,
    val audio: String,
    val inputFramerate: String,
    val inputResolution: String
)
