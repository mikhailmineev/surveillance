package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.util.stream.Feeder
import ru.mm.surv.codecs.webm.util.stream.Buffer
import java.io.InputStream

class StreamInput(private val stream: Stream, private val input: InputStream) {
    private val bufferSize = 65536

    fun run() {

        // notification about starting the input process
        stream.postEvent(ServerEvent(ServerEvent.INPUT_START))

        val buffer = Buffer(bufferSize)
        val feeder = Feeder(buffer, input)
        val hds = HeaderDetectionState(stream)
        feeder.feedTo(hds)
        val ss = StreamingState(stream, hds.videoTrackNumber)
        feeder.feedTo(ss)

        // notification about ending the input process
        stream.postEvent(ServerEvent(ServerEvent.INPUT_STOP))
    }
}