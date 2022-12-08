package ru.mm.surv.web

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import ru.mm.surv.capture.service.FfmpegStream
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.http.ResponseEntity
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import java.io.FileInputStream

@RestController
@RequestMapping("stream/thumb")
class ThumbController @Autowired constructor(private val ffmpegStream: FfmpegStream) {
    @GetMapping(value = ["/{stream}/thumb.jpg"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun thumb(@PathVariable("stream") stream: String): ResponseEntity<InputStreamResource> {
        return ffmpegStream
            .getThumb(stream)
            ?.let(::FileInputStream)
            ?.let(::InputStreamResource)
            ?.let { ResponseEntity.ok(it) }
            ?:ResponseEntity.notFound().build()
    }
}
