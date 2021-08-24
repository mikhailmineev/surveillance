package ru.mm.surv.web;

import lombok.extern.slf4j.Slf4j;
aimport org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.service.RecordService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("record/mp4")
@Slf4j
public class Mp4RecordController {

    private final RecordService recordService;

    public Mp4RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    public Collection<String> getRecords() {
        return recordService.getRecords();
    }

    @GetMapping(path = "/{record}/record.mp4", produces = "video/mp4")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable("record") String record) {
        return recordService
                .getMp4File(record)
                .map(FileSystemResource::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{record}/thumb.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> thumb(@PathVariable("record") String record) {
        return recordService.getThumb(record)
                .map(Path::toFile)
                .map(this::getFileInputStream)
                .map(InputStreamResource::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @NotNull
    private FileInputStream getFileInputStream(File e) {
        try {
            return new FileInputStream(e);
        } catch (FileNotFoundException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
