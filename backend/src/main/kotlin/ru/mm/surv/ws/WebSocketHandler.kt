package ru.mm.surv.ws

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList

@Component
class WebSocketHandler(
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    val sessions: MutableList<WebSocketSession> = CopyOnWriteArrayList()

    fun broadcast(entity: Any) {
        val serialized = objectMapper.writeValueAsBytes(entity)
        sessions.forEach { it.sendMessage(TextMessage(serialized)) }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }

}
