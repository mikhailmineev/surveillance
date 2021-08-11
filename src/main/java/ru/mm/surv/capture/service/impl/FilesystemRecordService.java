package ru.mm.surv.capture.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.service.RecordService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilesystemRecordService implements RecordService {

    private final Path recordsFolder;

    public FilesystemRecordService(@Value("${ffmpeg.mp4.folder}") Path recordsFolder) {
        this.recordsFolder = recordsFolder;
    }

    @Override
    public Collection<String> getRecords() throws IOException {
        return Files
                .list(recordsFolder)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}
