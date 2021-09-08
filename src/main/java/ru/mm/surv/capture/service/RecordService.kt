package ru.mm.surv.capture.service

import java.nio.file.Path

interface RecordService {
    fun records(): Collection<String>
    fun getMp4File(record: String): Path?
    fun getThumb(record: String): Path?
}
