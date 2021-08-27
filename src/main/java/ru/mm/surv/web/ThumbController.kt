package ru.mm.surv.web;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.capture.service.FfmpegStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("stream/thumb")
public class ThumbController {

    private final FfmpegStream ffmpegStream;

    @Autowired
    public ThumbController(FfmpegStream ffmpegStream) {
        this.ffmpegStream = ffmpegStream;
    }

    @GetMapping(value = "/{stream}/thumb.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> thumb(@PathVariable("stream") String stream) {
        return ffmpegStream
                .getThumb(stream)
                .map(this::createInputStream)
                .map(InputStreamResource::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @SneakyThrows
    private InputStream createInputStream(File file) {
        return new FileInputStream(file);
    }
}
