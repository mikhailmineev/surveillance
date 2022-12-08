package ru.mm.surv.capture.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.mm.surv.dto.StreamRecord
import ru.mm.surv.dto.StreamRecordVideo
import ru.mm.surv.web.fromNameList

class MappingTest {

    @Test
    fun mapVideoRecordArrayToVideoDto() {
        val videos = listOf("first-date1", "first-date2", "second-date1", "second-date2")
        val actual = fromNameList(videos)
        val expected = ArrayList<StreamRecord>()
        expected.add(StreamRecord("date1", listOf("first", "second").map { StreamRecordVideo(it, "date1") }))
        expected.add(StreamRecord("date2", listOf("first", "second").map { StreamRecordVideo(it, "date2") }))
        assertEquals(expected, actual)
    }
}
