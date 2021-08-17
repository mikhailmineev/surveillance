package ru.mm.surv.capture.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
class FfmpegLogger implements Runnable {

    private final String streamName;
    private final BufferedReader reader;

    public FfmpegLogger(String streamName, InputStream stream) {
        this.streamName = streamName;
        this.reader = new BufferedReader(new InputStreamReader(stream));
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("{} {}", streamName, line);
            }
        } catch (IOException e) {
            log.error(streamName + " " + e.getMessage(), e);
        }
    }
}
