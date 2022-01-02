package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.incubator.streamm.EBMLElement.Companion.loadUnsigned
import java.lang.RuntimeException
import ru.mm.surv.codecs.webm.util.stream.Processor

internal class HeaderDetectionState(private val stream: Stream) : Processor {
    var videoTrackNumber: Long = 0
        private set
    private var finished = false
    override fun process(buffer: ByteArray, offset: Int, length: Int): Int {
        var offset = offset
        val endOffset = offset + length
        val headerBuffer = ByteArray(65536)
        var headerLength = 0
        var elem: EBMLElement

        // EBML root element
        elem = try {
            EBMLElement(buffer, offset, length)
        } catch (e: RuntimeException) {
            // on EBML reading errors, need more data to be loaded
            return 0
        }

        // if not EBML
        if (elem.id != ID_EBML) throw RuntimeException("First element is not EBML!")

        // COPYING: EBML headerBuffer
        System.arraycopy(buffer, elem.elementOffset, headerBuffer, headerLength, elem.getElementSize())
        headerLength += elem.getElementSize()
        offset = elem.getEndOffset()

        // COPYING: infinite Segment
        System.arraycopy(infiniteSegment, 0, headerBuffer, headerLength, infiniteSegment.size)
        headerLength += infiniteSegment.size


        // looking for: Segment
        do {
            elem = EBMLElement(buffer, offset, length)
            if (elem.id == ID_SEGMENT) break
            offset = elem.getEndOffset()
        } while (offset < endOffset)

        // if not found ...
        if (offset >= endOffset) return 0
        val segmentDataOffset = elem.dataOffset

        // looking for: Info
        offset = segmentDataOffset
        do {
            elem = EBMLElement(buffer, offset, length)
            offset = elem.getEndOffset()
        } while (offset < endOffset && elem.id != ID_INFO)

        // if not found ...
        if (offset >= endOffset) return 0

        // COPYING: Info headerBuffer
        System.arraycopy(buffer, elem.elementOffset, headerBuffer, headerLength, elem.getElementSize())
        headerLength += elem.getElementSize()


        // looking for: Tracks
        offset = segmentDataOffset
        do {
            elem = EBMLElement(buffer, offset, length)
            offset = elem.getEndOffset()
        } while (offset < endOffset && elem.id != ID_TRACKS)

        // if not found ...
        if (offset >= endOffset) return 0

        // COPYING: Tracks headerBuffer
        System.arraycopy(buffer, elem.elementOffset, headerBuffer, headerLength, elem.getElementSize())
        headerLength += elem.getElementSize()

        // searching for video track's id
        val endOfTracks = elem.getEndOffset()
        offset = elem.dataOffset
        while (offset < endOfTracks) {
            val track = EBMLElement(buffer, offset, endOfTracks - offset)
            offset = track.dataOffset
            val endOfTrack = track.getEndOffset()
            var trackType: Long = 0
            var trackNumber: Long = 0
            while (offset < endOfTrack) {
                val property = EBMLElement(buffer, offset, endOfTrack - offset)
                if (property.id == ID_TRACKTYPE) {
                    trackType = buffer[property.dataOffset].toLong() and 0xff
                } else if (property.id == ID_TRACKNUMBER) {
                    trackNumber = loadUnsigned(buffer, property.dataOffset, property.dataSize.toInt())
                }
                offset = property.getEndOffset()
            }
            if (trackType == TRACK_TYPE_VIDEO) videoTrackNumber = trackNumber
            offset = track.getEndOffset()
        }

        // setting header for the stream
        val header = ByteArray(headerLength)
        System.arraycopy(headerBuffer, 0, header, 0, headerLength)
        stream.header = header
        finished = true
        return segmentDataOffset
    }

    override fun finished(): Boolean {
        return finished
    }

    companion object {
        private const val ID_EBML: Long = 0x1A45DFA3
        private const val ID_SEGMENT: Long = 0x18538067
        private const val ID_INFO: Long = 0x1549A966
        private const val ID_TRACKS: Long = 0x1654AE6B
        private const val ID_TRACKTYPE: Long = 0x83
        private const val ID_TRACKNUMBER: Long = 0xD7
        private const val TRACK_TYPE_VIDEO: Long = 1
        private val infiniteSegment = byteArrayOf(
            0x18,
            0x53,
            0x80.toByte(),
            0x67,
            0x01,
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )
    }
}
