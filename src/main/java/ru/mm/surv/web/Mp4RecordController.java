package ru.mm.surv.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.codecs.webm.incubator.streamm.ControlledStream;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("record/mp4")
@Slf4j
public class Mp4RecordController {

    private final Path recordsFolder;

    public Mp4RecordController(@Value("${ffmpeg.mp4.folder}") Path recordsFolder) {
        this.recordsFolder = recordsFolder;
    }
    @GetMapping(path = "/{fileName}", produces = "video/mp4")
    public ResponseEntity<InputStreamResource> getFile(
            @PathVariable("fileName") String fileName) throws HttpException {
        try {
            Path hlsFilePath = recordsFolder.resolve(fileName);
            InputStream is = new BufferedInputStream(new FileInputStream(hlsFilePath.toFile()));
            return ResponseEntity.ok(new InputStreamResource(is));
        } catch (FileNotFoundException e) {
            throw new HttpException(404, "File not found");
        }
    }
}
