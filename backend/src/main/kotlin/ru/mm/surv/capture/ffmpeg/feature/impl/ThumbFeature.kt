package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.ffmpeg.feature.Feature
import java.nio.file.Path

open class ThumbFeature(val path: Path): Feature {
    override fun args(): Array<String> {
        return arrayOf(
            "-ss", "00:00:01",
            "-vframes", "1",
            path.toString()
        )
    }

}
