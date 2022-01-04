package ru.mm.surv.web

import ru.mm.surv.dto.StreamRecord
import ru.mm.surv.dto.StreamRecordVideo
import java.util.stream.Collectors

const val RECORD_DATE = 1
const val RECORD_CAMERA_ID = 0

fun fromNameList(names: Collection<String>): Collection<StreamRecord> {
    return names.stream()
        .map { it.split("-", limit = 2) }
        .collect(
            Collectors.groupingBy({ it[RECORD_DATE] },
                { LinkedHashMap() },
                Collectors.mapping(
                    { StreamRecordVideo(it[RECORD_CAMERA_ID], it[RECORD_DATE]) },
                    Collectors.toList()
                )
            )
        )
        .entries
        .map { StreamRecord(it.key, it.value) }
}
