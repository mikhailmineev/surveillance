package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.FolderConfig
import java.nio.file.Files
import java.nio.file.Path

class Mp4ThumbOutput(captureConfig: CameraConfig, folders: FolderConfig, currentDate: String): ThumbFeature(
    createMp4Thumb(captureConfig, folders, currentDate)
) {

    companion object {
        private fun createMp4Thumb(captureConfig: CameraConfig, folders: FolderConfig, currentDate: String): Path {
            val mp4ThumbFolder = folders.mp4Thumb
            Files.createDirectories(mp4ThumbFolder)
            val mp4ThumbName = captureConfig.name + "-" + currentDate + ".jpg"
            return mp4ThumbFolder.resolve(mp4ThumbName)
        }
    }

}
