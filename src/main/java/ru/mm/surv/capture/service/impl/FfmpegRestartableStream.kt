package ru.mm.surv.capture.service.impl

import org.springframework.context.annotation.Primary
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Service
import ru.mm.surv.capture.service.FfmpegStream
import java.io.File
import javax.annotation.PreDestroy

@Service
@Primary
class FfmpegRestartableStream(
    private val ffmpegMultiStreamFactory: ObjectFactory<FfmpegMultiStream>) : FfmpegStream {
    private var ffmpegMultiStream: FfmpegMultiStream? = null

    @Synchronized
    override fun start() {
        if (!isActive()) {
            ffmpegMultiStream = ffmpegMultiStreamFactory.getObject()
        }
        ffmpegMultiStream!!.start()
    }

    @PreDestroy
    @Synchronized
    override fun stop() {
        if (isActive()) {
            ffmpegMultiStream!!.stop()
            ffmpegMultiStream = null
        }
    }

    override fun isActive(): Boolean {
        if (ffmpegMultiStream != null && !ffmpegMultiStream!!.isActive()) {
            ffmpegMultiStream = null
        }
        return ffmpegMultiStream != null
    }

    override fun streamNames(): Collection<String> {
        return ffmpegMultiStream
            ?.streamNames()
            ?: emptyList()
    }

    override fun getThumb(stream: String): File? {
        return ffmpegMultiStream?.getThumb(stream);
    }
}
