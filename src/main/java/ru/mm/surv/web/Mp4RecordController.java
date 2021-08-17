package ru.mm.surv.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.config.FolderConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("record/mp4")
@Slf4j
public class Mp4RecordController {

    private final FolderConfig folders;

    public Mp4RecordController(FolderConfig folders) {
        this.folders = folders;
    }

    @GetMapping(path = "/{record}/record.mp4", produces = "video/mp4")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable("record") String record) {
        Path path = folders.getMp4().resolve(record + ".mp4");
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new FileSystemResource(path));
    }

    @GetMapping(path = "/{record}/thumb.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> thumb(@PathVariable("record") String record) throws FileNotFoundException {
        Path path = folders.getMp4Thumb().resolve(record + ".jpg");
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        InputStream inputStream = new FileInputStream(path.toFile());
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok().body(inputStreamResource);
    }
}
