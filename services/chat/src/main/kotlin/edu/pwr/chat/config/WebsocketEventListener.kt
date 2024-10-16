import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener {

    // In-memory map of chatId -> list of sessions (users' WebSocket sessions)
    private val chatSessions = mutableMapOf<Long, MutableList<String>>()

    // On new WebSocket connection
    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = headerAccessor.sessionId
        val chatId = headerAccessor.getFirstNativeHeader("chatId")?.toLong()

        if (sessionId != null && chatId != null) {
            chatSessions.computeIfAbsent(chatId) { mutableListOf() }.add(sessionId)
            println("Session $sessionId connected to chatId $chatId")
        }
    }

    // On WebSocket disconnection
    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = headerAccessor.sessionId

        // Remove the session from all chat rooms it was part of
        chatSessions.forEach { (chatId, sessions) ->
            sessions.remove(sessionId)
            if (sessions.isEmpty()) {
                chatSessions.remove(chatId)
            }
        }

        println("Session $sessionId disconnected")
    }

    // Returns the chat sessions map for testing purposes
    fun getChatSessions() = chatSessions
}
