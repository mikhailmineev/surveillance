package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.FolderConfig
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class StreamThumbOutput(captureConfig: CameraConfig, folders: FolderConfig): ThumbFeature(createStreamThumb(captureConfig, folders)) {

    companion object {
        private fun createStreamThumb(captureConfig: CameraConfig, folders: FolderConfig): Path {
            val streamThumbFolder = folders.streamThumb
            Files.createDirectories(streamThumbFolder)
            Files.walk(streamThumbFolder)
                .filter { Files.isRegularFile(it) }
                .map(Path::toFile)
                .forEach(File::delete)
            val streamThumbName = captureConfig.name + ".jpg"
            return streamThumbFolder.resolve(streamThumbName)
        }
    }

}
