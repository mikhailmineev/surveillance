package ru.mm.surv.codecs.webm.util.stream

/**
 *
 * @author bence
 */
interface Processor {
    /**
     * Checks if this processor has finished its job.
     *
     * @return `true` if this processor extracted all the data.
     */
    fun finished(): Boolean

    /**
     * Process binary data from this buffer.
     *
     * @param buffer The buffer of data.
     * @param offset The offset of the first byte which needs to be processed within the buffer.
     * @param length The number of data bytes need to be processed.
     * @return Number of bytes successfully processed.
     */
    fun process(buffer: ByteArray, offset: Int, length: Int): Int
}
