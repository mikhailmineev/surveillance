package ru.mm.surv.capture.config

data class InputSource(
    val type: InputType,
    val id: String,
    val name: String,
    val formats: List<InputFormat>,
)
