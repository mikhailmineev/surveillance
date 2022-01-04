package ru.mm.surv.dto

data class StreamRecordVideo(
    val name: String,
    val cameraId: String,
    val date: String
) {
    constructor(cameraId: String, date: String) : this("$cameraId-$date", cameraId, date)
}
