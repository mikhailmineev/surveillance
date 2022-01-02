package ru.mm.surv.codecs.webm.util.stream

import ru.mm.surv.codecs.webm.event.EventSource
import ru.mm.surv.codecs.webm.incubator.streamm.Stream
import java.io.OutputStream

/**
 * Decorates an OutputStream object, generating TransferEvents on writes. Data
 * is being written in chunks to achieve a better time-resolution with the
 * events. Smaller chunk sizes lead to better resolution on cost of resources.
 *
 * To avoid unnecessary resource usage the last *chunkSize / 2* bytes are
 * written together with the last chunk.
 *
 * @author Varga Bence
 */
class MeasuredOutputStream(private val base: OutputStream, private val source: Stream, private val chunkSize: Int) : OutputStream() {

    override fun write(i: Int) {
        val buffer = byteArrayOf(i.toByte())
        write(buffer, 0, 1)
    }

    override fun write(buffer: ByteArray) {
        write(buffer, 0, buffer.size)
    }

    override fun write(buffer: ByteArray, initialOffset: Int, initialLength: Int) {
        var offset = initialOffset
        var length = initialLength
        while (length > 0) {

            // current packet size
            var fragLength = length
            if (fragLength >= 1.5 * chunkSize) fragLength = chunkSize

            // writing data packet
            base.write(buffer, offset, fragLength)

            // notification about the transfer
            source.postEvent(TransferEvent(TransferEvent.STREAM_OUTPUT))

            // next packet (chunk) start
            offset += fragLength
            length -= fragLength
        }
    }
}