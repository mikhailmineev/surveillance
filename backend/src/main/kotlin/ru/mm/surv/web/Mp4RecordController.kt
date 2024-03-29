package ru.mm.surv.web

import lombok.extern.slf4j.Slf4j
import ru.mm.surv.capture.service.RecordService
import org.springframework.http.ResponseEntity
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.mm.surv.dto.StreamRecord
import java.io.FileInputStream

@RestController
@RequestMapping("record/mp4")
@Slf4j
class Mp4RecordController(private val recordService: RecordService) {

    @GetMapping
    fun records() : Collection<StreamRecord> {
        return fromNameList(recordService.records())
    }

    @GetMapping(path = ["/{record}/record.mp4"], produces = ["video/mp4"])
    fun getFile(@PathVariable("record") record: String): ResponseEntity<FileSystemResource> {
        return recordService
            .getMp4File(record)
            ?.let(::FileSystemResource)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping(path = ["/{record}/record.mp4"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFile(@PathVariable("record") record: String) {
        recordService.deleteMp4File(record)
    }

    @GetMapping(path = ["/{record}/thumb.jpg"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun thumb(@PathVariable("record") record: String): ResponseEntity<InputStreamResource> {
        return recordService.getThumb(record)
            ?.toFile()
            ?.let(::FileInputStream)
            ?.let(::InputStreamResource)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}
