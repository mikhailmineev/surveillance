/*
 * Copyright 2018 Bence Varga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.mm.surv.codecs.webm.util.stream

import kotlin.Throws
import java.io.IOException
import java.io.InputStream

/**
 * A facility which feeds the data from an `InputStream` to a `Processor` until
 * the `Processor` finishes its work.
 *
 * Since data is being red in chunks, in most cases a surplus of data is red. This *unprocessed* data
 * will be kept in the buffer after each `feedTo` is done and will be available for subsequent
 * calls.
 ** */
class Feeder(val buffer: Buffer, private val input: InputStream) {
    /**
     * Feeds the input to a `Processor` until it finishes its work.
     */
    fun feedTo(processor: Processor) {
        val data = buffer.data
        do {
            if (buffer.length > 0) {
                // processing current payload and signalling the buffer that the processed data can be discarded
                val bytesProcessed = processor.process(data, buffer.offset, buffer.length)
                buffer.markProcessed(bytesProcessed)
            }
            if (processor.finished()) {
                return
            }

            // compacting buffer (moving payload to the start of the buffer) to maximize space for input data
            buffer.compact()

            // reading new data and notifying buffer about the new content
            val length = buffer.length
            val bytes = input.read(data, length, data.size - length)

            // no more data to read
            if (bytes == -1) return
            buffer.markAppended(bytes)
        } while (true)
    }
}