package ru.mm.surv.dto

data class StreamRecord(
    val date: String,
    val videos: List<StreamRecordVideo>,
)
