package semsem.chatbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.chatbot.model.dto.request.ChatRequestDto;
import semsem.chatbot.model.dto.response.ChatResponseDto;
import semsem.chatbot.model.dto.response.MyApiResponse;
import semsem.chatbot.service.chat.LLMChattingService;


/**
 * Controller for chat/LLM interactions.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat and LLM interaction endpoints")
public class ChatController {

    private final LLMChattingService llmChattingService;

    @PostMapping
    @Operation(summary = "Send a chat message", description = "Process a user message through the AI chatbot workflow")
    public ResponseEntity<MyApiResponse<ChatResponseDto>> chat(@Valid @RequestBody ChatRequestDto request) {
        ChatResponseDto response = llmChattingService.askLLM(request);
        return ResponseEntity.ok(MyApiResponse.success(response));
    }
}