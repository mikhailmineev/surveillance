package ru.mm.surv.capture.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.config.FolderConfig;
import ru.mm.surv.capture.service.RecordService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class FilesystemRecordService implements RecordService {

    private final Path recordsFolder;

    public FilesystemRecordService(FolderConfig folders) {
        this.recordsFolder = folders.getMp4();
    }

    @Override
    public Collection<String> getRecords() throws IOException {
        if (!Files.exists(recordsFolder)){
            return Collections.emptyList();
        }
        return Files
                .list(recordsFolder)
                .map(Path::getFileName)
                .map(Path::toString)
                .map(FilenameUtils::removeExtension)
                .collect(Collectors.toList());
    }
}
