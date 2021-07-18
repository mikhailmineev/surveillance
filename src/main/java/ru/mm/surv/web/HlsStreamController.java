package ru.mm.surv.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/{streamId}/{fileName}", method = RequestMethod.GET)
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
    }}
