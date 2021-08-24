package ru.mm.surv.capture.service;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface RecordService {

    Collection<String> getRecords();

    Optional<Path> getMp4File(String record);

    Optional<Path> getThumb(String record);
}
