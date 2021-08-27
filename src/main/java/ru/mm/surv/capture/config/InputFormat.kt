package ru.mm.surv.capture.config

import lombok.Data

@Data
data class InputFormat(
    val resolution: String,
    val fps: String
)
