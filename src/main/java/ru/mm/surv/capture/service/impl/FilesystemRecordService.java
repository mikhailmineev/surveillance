package ru.mm.surv.capture.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.config.FolderConfig;
import ru.mm.surv.capture.service.RecordService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class FilesystemRecordService implements RecordService {

    private final FolderConfig folders;

    public FilesystemRecordService(FolderConfig folders) {
        this.folders = folders;
    }

    @Override
    public Collection<String> records() {
        var recordsFolder = folders.getMp4();
        if (!Files.exists(recordsFolder)){
            return Collections.emptyList();
        }
        try {
            return Files
                    .list(recordsFolder)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(FilenameUtils::removeExtension)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Path getMp4File(@NotNull String record) {
        var recordsFolder = folders.getMp4();
        var path = recordsFolder.resolve(record + ".mp4");
        if (!Files.exists(path)) {
            return null;
        }
        return path;
    }

    @Override
    public Path getThumb(@NotNull String record) {
        var thumbsFolder = folders.getMp4Thumb();
        var path = thumbsFolder.resolve(record + ".jpg");
        if (!Files.exists(path)) {
            return null;
        }
        return path;
    }
}
