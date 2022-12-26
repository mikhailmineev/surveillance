package ru.mm.surv.capture.event

import org.springframework.context.ApplicationEvent
import ru.mm.surv.dto.StreamStatus

class StreamStatusEvent(val status: StreamStatus, val streams: Collection<String>, source: Any): ApplicationEvent(source)
