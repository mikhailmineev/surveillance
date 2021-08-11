package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("stream/hls")
public class HlsStreamController {

    private final Path hlsFolder;

    public HlsStreamController(@Value("${ffmpeg.hls.folder}") Path hlsFolder) {
        this.hlsFolder = hlsFolder;
    }

    @GetMapping(value = "/{streamId}/{fileName}", produces = "application/vnd.apple.mpegurl")
    public ResponseEntity<FileSystemResource> getFile(
            @PathVariable("streamId") String streamId,
            @PathVariable("fileName") String fileName) {
        Path hlsFilePath = hlsFolder.resolve(streamId).resolve(fileName);
        return ResponseEntity.ok(new FileSystemResource(hlsFilePath));
    }
}
