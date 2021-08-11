package ru.mm.surv.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("record/mp4")
@Slf4j
public class Mp4RecordController {

    private final Path recordsFolder;

    public Mp4RecordController(@Value("${ffmpeg.mp4.folder}") Path recordsFolder) {
        this.recordsFolder = recordsFolder;
    }

    @GetMapping(path = "/{fileName}", produces = "video/mp4")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable("fileName") String fileName) {
        Path hlsFilePath = recordsFolder.resolve(fileName);
        return ResponseEntity.ok(new FileSystemResource(hlsFilePath));
    }
}
