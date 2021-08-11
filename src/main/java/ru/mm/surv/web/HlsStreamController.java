package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

@RestController
@RequestMapping("stream/hls")
public class HlsStreamController {

    private final Path hlsFolder;

    public HlsStreamController(@Value("${ffmpeg.hls.folder}") Path hlsFolder) {
        this.hlsFolder = hlsFolder;
    }

    @GetMapping(value = "/{streamId}/{fileName}", produces = "application/vnd.apple.mpegurl")
    public ResponseEntity<InputStreamResource> getFile(
            @PathVariable("streamId") String streamId,
            @PathVariable("fileName") String fileName) throws HttpException {
        try {
            Path hlsFilePath = hlsFolder.resolve(streamId).resolve(fileName);
            InputStream is = new BufferedInputStream(new FileInputStream(hlsFilePath.toFile()));
            return ResponseEntity.ok(new InputStreamResource(is));
        } catch (FileNotFoundException e) {
            throw new HttpException(404, "Stream Not Running");
        }
    }
}
