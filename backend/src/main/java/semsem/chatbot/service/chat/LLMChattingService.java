package semsem.chatbot.service.chat;

import semsem.chatbot.model.dto.request.ChatRequestDto;
import semsem.chatbot.model.dto.response.ChatResponseDto;

public interface LLMChattingService {
    ChatResponseDto askLLM(ChatRequestDto request);
}
