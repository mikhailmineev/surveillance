package ru.mm.surv.capture.service.impl

import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.service.RecordService
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
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
        return mp4FilePath(record).takeIf { Files.exists(it) }
    }

    override fun getThumb(record: String): Path? {
        return thumbPath(record).takeIf { Files.exists(it) }
    }

    override fun deleteMp4File(record: String) {
        mp4FilePath(record).deleteIfExists()
        thumbPath(record).deleteIfExists()
    }

    private fun thumbPath(record: String) = folders.mp4Thumb.resolve("$record.jpg")

    private fun mp4FilePath(record: String) = folders.mp4.resolve("$record.mp4")
}
