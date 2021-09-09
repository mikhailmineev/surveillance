package ru.mm.surv.codecs.webm.util.stream

import java.lang.RuntimeException

/**
 * A byte array based buffer. The payload can be an arbitrary region of the array.
 */
class Buffer(val data: ByteArray) {

    /**
     * Creates a StreamBuffer with the given size.
     *
     * @param size Desired capacity of the buffer.
     */
    constructor(size: Int) : this(ByteArray(size))

    /**
     * Creates a StreamBuffer backed by the given array. Contents
     * of the given array will be changed by the object.
     *
     * @param buffer Array to use.
     * @param offset Beginning of data in the buffer.
     * @param length Length of data.
     */
    constructor(buffer: ByteArray, offset: Int, length: Int) : this(buffer) {
        this.offset = offset
        this.length = length
    }

    /**
     * Gets the offset of the first byte containing the data of this buffer
     * inside the buffer array.
     */
    var offset = 0
        private set

    /**
     * Gets the number of bytes containing the data of this buffer.
     */
    var length = 0
        private set

    /**
     * Moves the payload of this buffer to the start of the array.
     */
    fun compact() {
        System.arraycopy(data, offset, data, 0, length)
        offset = 0
    }

    /**
     * Signals that the first `numOfBytes` bytes has been processed
     * and should not be considered as part of the payload anymore.
     * @param numOfBytes The number of bytes processed.
     */
    fun markProcessed(numOfBytes: Int) {
        if (numOfBytes < 0) throw RuntimeException("Negative number of bytes is unsupported: $numOfBytes")
        if (numOfBytes > length) throw RuntimeException("Region too big: $numOfBytes (length is: $length).")
        length -= numOfBytes
        offset += numOfBytes
    }

    /**
     * Signals that `numOfBytes` bytes of new data has been appended
     * to the end of the payload.
     * @param numOfBytes The number of bytes appended.
     */
    fun markAppended(numOfBytes: Int) {
        if (numOfBytes < 0) throw RuntimeException("Negative number of bytes is unsupported: $numOfBytes")
        if (numOfBytes > data.size - (offset + length)) throw RuntimeException("Region too big: " + numOfBytes + " (empty space is: " + (data.size - (offset + length)) + ").")
        length += numOfBytes
    }
}
