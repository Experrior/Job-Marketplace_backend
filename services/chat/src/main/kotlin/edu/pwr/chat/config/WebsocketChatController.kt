import edu.pwr.chat.entities.ChatMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WebSocketChatController(private val messagingTemplate: SimpMessagingTemplate) {
    // This method is invoked when a message is sent to "/app/chat"
    @MessageMapping("/chat")
    fun handleMessage(chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor?) {
        val chatId: Int? = chatMessage.chatId // Extract chatId from the message
        val destination = "/topic/chat/$chatId" // Send message to all users in the chat

        // Broadcast the message to all users connected to this chatId
        messagingTemplate.convertAndSend(destination, chatMessage)
    }
}