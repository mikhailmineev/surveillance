package ru.mm.surv.codecs.webm.util.stream

import ru.mm.surv.codecs.webm.event.EventSource
import java.net.SocketTimeoutException
import java.io.InputStream

class MeasuredInputStream(private val base: InputStream, private val source: EventSource) : InputStream() {

    override fun read(): Int {
        val buffer = ByteArray(1)
        val bytes = read(buffer, 0, 1)
        return buffer[0].toInt()
    }

    override fun read(buffer: ByteArray, offset: Int, maxLength: Int): Int {
        var numBytes = 0
        while (numBytes == 0) {
            try {
                numBytes = base.read(buffer, offset, maxLength)
            } catch (e: SocketTimeoutException) {
            }
        }
        source.postEvent(TransferEvent(TransferEvent.STREAM_INPUT))
        return numBytes
    }
}