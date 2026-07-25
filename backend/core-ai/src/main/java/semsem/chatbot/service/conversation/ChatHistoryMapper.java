package semsem.chatbot.service.conversation;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.orchestration.dbchat.state.ChatMessage;

import java.util.List;
import java.util.Optional;

@Component
public class ChatHistoryMapper {

    private static final int MAX_HISTORY_MESSAGES = 5;

    public List<Message> toRecentMessages(List<ChatMessage> history) {
        return Optional.ofNullable(history)
                .filter(list -> !list.isEmpty())
                .map(list -> list.subList(Math.max(0, list.size() - MAX_HISTORY_MESSAGES), list.size())
                        .stream()
                        .map(this::toMessage)
                        .toList())
                .orElseGet(List::of);
    }

    private Message toMessage(ChatMessage message) {
        return message.getRole() == MessageRole.ASSISTANT
                ? new AssistantMessage(message.getContent())
                : new UserMessage(message.getContent());
    }
}