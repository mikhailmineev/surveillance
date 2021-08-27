package ru.mm.surv.web

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import ru.mm.surv.capture.config.FolderConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.http.ResponseEntity
import org.springframework.core.io.FileSystemResource
import java.nio.file.Files
import java.nio.file.Path

@RestController
@RequestMapping("stream/hls")
class HlsStreamController(folders: FolderConfig) {
    private val hlsFolder: Path = folders.hls

    @GetMapping(value = ["/{streamId}/{fileName}"], produces = ["application/vnd.apple.mpegurl"])
    fun getFile(
        @PathVariable("streamId") streamId: String,
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<FileSystemResource> {
        return hlsFolder.resolve(streamId).resolve(fileName)
            .takeIf(Files::exists)
            ?.let(::FileSystemResource)
            ?.let { ResponseEntity.ok(it) }
            ?:ResponseEntity.notFound().build()
    }
}
