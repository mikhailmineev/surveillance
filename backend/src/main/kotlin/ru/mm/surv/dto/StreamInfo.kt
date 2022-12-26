package ru.mm.surv.dto

data class StreamInfo(
    val streamStatus: StreamStatus,
    val streams: Collection<String>
)
