package ru.mm.surv.capture.event

import org.springframework.context.ApplicationEvent
import ru.mm.surv.dto.StreamStatus

class StreamStatusEvent(val status: StreamStatus, source: Any): ApplicationEvent(source)
