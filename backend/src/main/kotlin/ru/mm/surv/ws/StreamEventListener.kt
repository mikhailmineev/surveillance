package ru.mm.surv.ws

import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.mm.surv.capture.event.StreamStatusEvent
import ru.mm.surv.dto.StreamInfo

@Component
class StreamEventListener(private val handler: WebSocketHandler): ApplicationListener<StreamStatusEvent> {

    override fun onApplicationEvent(event: StreamStatusEvent) {
        handler.broadcast(StreamInfo(event.status, event.streams))
    }

}
