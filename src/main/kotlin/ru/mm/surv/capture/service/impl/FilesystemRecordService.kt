package ru.mm.surv.capture.service.impl

import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.service.RecordService
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

@Service
class FilesystemRecordService(private val folders: FolderConfig) : RecordService {

    override fun records(): Collection<String> {
        return folders.mp4.takeIf { Files.exists(it) }
            ?.let { Files.list(it) }
            ?.map { it.fileName }
            ?.map(Path::toString)
            ?.map { FilenameUtils.removeExtension(it) }
            ?.sorted(sorter())
            ?.toList()
            ?: emptyList()
    }

    private fun sorter(): Comparator<String> {
        return Comparator { a, b ->
            val dateA = a.split("-", limit = 2)[1]
            val dateB = b.split("-", limit = 2)[1]
            - dateA.compareTo(dateB)
        }
    }

    override fun getMp4File(record: String): Path? {
        return folders.mp4.resolve("$record.mp4").takeIf { Files.exists(it) }
    }

    override fun getThumb(record: String): Path? {
        return folders.mp4Thumb.resolve("$record.jpg").takeIf { Files.exists(it) }
    }
}
