package ru.mm.surv.capture.service.impl

import org.springframework.context.annotation.Primary
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Service
import ru.mm.surv.capture.service.FfmpegStream
import ru.mm.surv.dto.StreamStatus
import java.io.File
import javax.annotation.PreDestroy

@Service
@Primary
class FfmpegRestartableStream(
    private val ffmpegMultiStreamFactory: ObjectFactory<FfmpegMultiStream>) : FfmpegStream {
    private var ffmpegMultiStream: FfmpegMultiStream? = null

    @Synchronized
    override fun start() {
        if (status() == StreamStatus.STOPPED) {
            ffmpegMultiStream = ffmpegMultiStreamFactory.getObject()
        }
        ffmpegMultiStream!!.start()
    }

    @PreDestroy
    @Synchronized
    override fun stop() {
        if (status() == StreamStatus.RUNNING) {
            ffmpegMultiStream!!.stop()
            ffmpegMultiStream = null

        }
    }

    override fun status(): StreamStatus {
        if (ffmpegMultiStream == null) {
            return StreamStatus.STOPPED
        }
        return ffmpegMultiStream!!.status()
    }

    override fun streamNames(): Collection<String> {
        return ffmpegMultiStream
            ?.streamNames()
            ?: emptyList()
    }

    override fun getThumb(stream: String): File? {
        return ffmpegMultiStream?.getThumb(stream)
    }
}
