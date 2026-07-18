package semsem.chatbot.service.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semsem.chatbot.exception.ApiException;
import semsem.chatbot.model.dto.request.ChatRequestDto;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.ChatResponseDto;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.workflow.GraphOrchestrator;
import semsem.chatbot.service.chat.LLMChattingService;
import semsem.chatbot.service.chat.MessageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LLMChattingServiceImpl implements LLMChattingService {

    private final GraphOrchestrator<ChatState> chatGraph;
    private final MessageService messageService;

    @Value("${chatbot.debug:false}")
    private boolean debugMode;

    @Override
    public ChatResponseDto askLLM(ChatRequestDto request) {
        try {
            log.info("Received chat request: {}", request.getMessage());

            Long conversationId = Optional.ofNullable(request.getConversationId())
                    .filter(id -> !id.equals(null))
                    .orElseThrow(() -> new ApiException("Conversation ID is required"));

            // Persist the user's message before running the workflow.
            persistMessage(conversationId, MessageRole.USER, request.getMessage(), null);

            Map<String, Object> input = Map.of(
                    ChatState.Keys.CONVERSATION_ID, conversationId,
                    ChatState.Keys.USER_QUERY, request.getMessage(),
                    ChatState.Keys.MESSAGES, List.of(ChatMessage.user(request.getMessage())));

            long startedAt = System.currentTimeMillis();
            ChatState resultState = chatGraph.run(input, String.valueOf(conversationId));
            long latencyMs = System.currentTimeMillis() - startedAt;

            // Persist the assistant's reply so the conversation history reloads.
            persistMessage(conversationId, MessageRole.ASSISTANT, resultState.response(), latencyMs);

            // Build response
            return buildResponse(resultState, conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error processing chat request: {}", ex.getMessage(), ex);
            throw new ApiException("Failed to process chat request: " + ex.getMessage());
        }
    }

    /**
     * Best-effort persistence of a chat message. A storage failure is logged but
     * never propagated, so it can't break an otherwise successful chat response.
     */
    private void persistMessage(Long conversationId, MessageRole role, String content, Long latencyMs) {
        String safeContent = (content == null || content.isBlank()) ? "(no response)" : content;
        try {
            messageService.createMessage(
                    conversationId,
                    CreateMessageRequest.builder()
                            .role(role)
                            .content(safeContent)
                            .latencyMs(latencyMs)
                            .build()
            );
        } catch (Exception ex) {
            log.warn("Failed to persist {} message for conversation {}: {}",
                    role, conversationId, ex.getMessage());
        }
    }

    private ChatResponseDto buildResponse(ChatState state, Long conversationId) {
        try {
            ChatResponseDto.ChatResponseDtoBuilder builder = ChatResponseDto.builder()
                    .message(state.response())
                    .conversationId(conversationId);

            // Add intent info
            Optional.ofNullable(state.analysis())
                    .map(QueryAnalyzerOutput::getIntent)
                    .ifPresent(intent -> {
                        builder.intent(intent.getName());
                        builder.intentConfidence(intent.getConfidence());
                    });

            // Add result info
            Optional.ofNullable(state.sqlExecution())
                    .ifPresent(sqlOutput -> {
                        builder.resultCount(sqlOutput.getRowCount());
                        builder.executionTimeMs(sqlOutput.getExecutionTimeMs());
                        if (sqlOutput.isSuccess() && sqlOutput.getRowCount() > 0) {
                            builder.data(sqlOutput.getResults());
                        }
                    });

            // Add debug info if enabled
            if (debugMode) {
                builder.debug(buildDebugInfo(state));
            }

            return builder.build();
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error building chat response: {}", ex.getMessage(), ex);
            throw new ApiException("Failed to build chat response: " + ex.getMessage());
        }
    }

    private ChatResponseDto.DebugInfo buildDebugInfo(ChatState state) {
        try {
            ChatResponseDto.DebugInfo.DebugInfoBuilder debug = ChatResponseDto.DebugInfo.builder();

            Optional.ofNullable(state.sqlGeneration())
                    .ifPresent(sql -> {
                        debug.generatedSql(sql.getGeneratedSql());
                        debug.sqlParameters(sql.getParameters());
                    });

            Optional.ofNullable(state.analysis())
                    .map(QueryAnalyzerOutput::getEntities)
                    .ifPresent(debug::extractedEntities);

            return debug.build();
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error building debug info: {}", ex.getMessage(), ex);
            throw new ApiException("Failed to build debug info: " + ex.getMessage());
        }
    }
}