package semsem.chatbot.chat.service;

import semsem.chatbot.chat.dto.ChatRequestDto;
import semsem.chatbot.chat.dto.ChatResponseDto;

public interface LLMChattingService {
    ChatResponseDto askLLM(ChatRequestDto request);
}
