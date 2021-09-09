package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.util.stream.Buffer
import java.lang.RuntimeException

class MatroskaFragment : MovieFragment {
    private val data = ByteArray(MovieFragment.LIMIT_FRAME_MAXIMUM)

    private var dataLength = 0
    private var clusterOffset = -1

    fun openCluster(timeCode: Long) {
        if (clusterOffset != -1) closeCluster()
        System.arraycopy(clusterHead, 0, data, dataLength, clusterHead.size)
        clusterOffset = dataLength
        dataLength += clusterHead.size

        // saving timeCode
        var offset = clusterOffset + TIMECODE_LAST_OFFSET
        var num: Long
        num = timeCode
        while (num > 0) {
            data[offset--] = num.toByte()
            num = num shr 8
        }
    }

    fun closeCluster() {
        if (clusterOffset == -1) throw RuntimeException("No open cluster.")

        // cluster length (including initial TimeCode element)
        val clusterLength = dataLength - clusterOffset - INITIAL_CLUSTER_LENGTH

        // saving cluster length to the EBML element's header
        var offset = clusterOffset + CLUSTER_LENGTH_LAST_OFFSET
        var num = clusterLength
        while (num > 0) {
            data[offset--] = num.toByte()
            num = num shr 8
        }
        clusterOffset = -1
    }

    fun appendKeyBlock(buffer: ByteArray, offset: Int, length: Int) {
        appendBlock(buffer, offset, length)
    }

    fun appendBlock(buffer: ByteArray, offset: Int, length: Int) {
        if (data.size < dataLength + length) throw RuntimeException("Buffer full")
        System.arraycopy(buffer, offset, data, dataLength, length)
        dataLength += length
    }

    override fun getBuffers(): Array<Buffer> {
        return arrayOf(Buffer(data, 0, dataLength))
    }

    override fun length(): Int {
        return dataLength
    }

    companion object {
        private const val INITIAL_CLUSTER_LENGTH = 9
        private const val TIMECODE_LAST_OFFSET = 18
        private const val CLUSTER_LENGTH_LAST_OFFSET = 8
        private val clusterHead = byteArrayOf(0x1F, 0x43, 0xB6.toByte(), 0x75, 0x08, 0, 0, 0, 0,
                0xe7.toByte(), 0x88.toByte(), 0, 0, 0, 0, 0, 0, 0, 0)
    }
}