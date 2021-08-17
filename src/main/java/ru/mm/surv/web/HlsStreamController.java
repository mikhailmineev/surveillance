package ru.mm.surv.web;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.config.FolderConfig;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("stream/hls")
public class HlsStreamController {

    private final Path hlsFolder;

    public HlsStreamController(FolderConfig folders) {
        this.hlsFolder = folders.getHls();
    }

    @GetMapping(value = "/{streamId}/{fileName}", produces = "application/vnd.apple.mpegurl")
    public ResponseEntity<FileSystemResource> getFile(
            @PathVariable("streamId") String streamId,
            @PathVariable("fileName") String fileName) {
        Path path = hlsFolder.resolve(streamId).resolve(fileName);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new FileSystemResource(path));
    }
}
