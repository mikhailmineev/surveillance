package ru.mm.surv.codecs.webm.incubator.streamm

class EBMLElement(
    private val buffer: ByteArray,
    elementOffsetTemp: Int,
    length: Int,
) {
    val id: Long
    val elementOffset: Int
    val dataSize: Long
    val dataOffset: Int

    init {
        var elementOffsetTemp = elementOffsetTemp
        if (length < 2) {
            throw RuntimeException("Partial header (buffered sample too small).")
        }
        val limit = elementOffsetTemp + length
        var sizeFlag = 0x80L
        var num = 0L
        while ((run {
                num = num or buffer[elementOffsetTemp++].toLong() and 0xff
                num
            } and sizeFlag) == 0L && sizeFlag != 0L) {
            if (elementOffsetTemp >= limit) {
                throw RuntimeException("Partial header (buffered sample too small).")
            }
            num = num shl 8
            sizeFlag = sizeFlag shl 7
        }
        id = num
        sizeFlag = 0x80
        num = 0
        while ((run {
                num = num or buffer[elementOffsetTemp++].toLong() and 0xff
                num
            } and sizeFlag) == 0L && sizeFlag != 0L) {
            if (elementOffsetTemp >= limit) {
                throw RuntimeException("Partial header (buffered sample too small).")
            }
            num = num shl 8
            sizeFlag = sizeFlag shl 7
        }
        dataSize = num or sizeFlag
        dataOffset = elementOffsetTemp
        elementOffset = elementOffsetTemp
    }

    fun getElementSize(): Int {
        if (dataSize == 0x1ffffffffffffffL)
            return -1

        if (dataSize >= 0x100000000L)
            throw RuntimeException("Element too long to get array offset.")

        return (dataOffset - elementOffset + dataSize).toInt()
    }

    fun getEndOffset(): Int {
        if (dataSize == 0x1ffffffffffffffL)
            return -1

        if ((dataOffset + dataSize) >= 0x100000000L)
            throw RuntimeException("Element too long to get array offset.")

        return (dataOffset + dataSize).toInt()
    }

    override fun toString(): String {
        return "EBMLElement ID:0x ${id.toString(16)} size: $dataSize"
    }

    companion object {
        @JvmStatic
        fun loadUnsigned(buffer: ByteArray, offset1: Int, length1: Int): Long {
            var offset = offset1
            var length = length1
            var num = 0L
            while (length > 0) {
                length--
                num = num shl 8
                num = num or buffer[offset++].toLong() and 0xff
            }

            return num
        }
    }
}
