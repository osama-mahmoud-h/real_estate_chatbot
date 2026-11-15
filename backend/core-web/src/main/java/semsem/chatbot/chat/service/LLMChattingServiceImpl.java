package semsem.chatbot.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semsem.chatbot.chat.dto.ChatRequestDto;
import semsem.chatbot.chat.dto.ChatResponseDto;
import semsem.chatbot.common.exception.ApiException;
import semsem.chatbot.conversation.dto.CreateMessageRequest;
import semsem.chatbot.conversation.service.MessageService;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.orchestration.common.GraphOrchestrator;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.state.ChatMessage;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LLMChattingServiceImpl implements LLMChattingService {

    private final GraphOrchestrator<DbChatState> chatGraph;
    private final MessageService messageService;

    @Value("${chatbot.debug:false}")
    private boolean debugMode;

    @Override
    public ChatResponseDto askLLM(ChatRequestDto request) {
        try {
            Long conversationId = Optional.ofNullable(request)
                    .map(ChatRequestDto::getConversationId)
                    .orElseThrow(() -> {
                        log.warn("Rejected chat request: conversationId is null");
                        return new ApiException("Conversation ID is required");
                    });

            log.info("Received chat request [conversationId={}, messageLength={}]",
                    conversationId, request.getMessage() != null ? request.getMessage().length() : 0);
            log.debug("Chat request payload [conversationId={}, debugMode={}, message='{}']",
                    conversationId, debugMode, request.getMessage());

            // Persist the user's message before running the workflow.
            persistMessage(conversationId, MessageRole.USER, request.getMessage(), null);

            Map<String, Object> input = Map.of(
                    DbChatState.Keys.CONVERSATION_ID, conversationId,
                    DbChatState.Keys.USER_QUERY, request.getMessage(),
                    DbChatState.Keys.MESSAGES, List.of(ChatMessage.user(request.getMessage())));

            long startedAt = System.currentTimeMillis();
            DbChatState resultState = chatGraph.run(input, String.valueOf(conversationId));
            long latencyMs = System.currentTimeMillis() - startedAt;

            if (resultState.response() == null || resultState.response().isBlank()) {
                log.warn("Chat graph completed in {}ms for conversationId={} but returned an empty response",
                        latencyMs, conversationId);
            } else {
                log.info("Chat workflow completed [conversationId={}, latency={}ms, responseLength={}]",
                        conversationId, latencyMs, resultState.response().length());
            }

            // Persist the assistant's reply so the conversation history reloads.
            persistMessage(conversationId, MessageRole.ASSISTANT, resultState.response(), latencyMs);

            // Build response
            return buildResponse(resultState, conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error processing chat request [conversationId={}]: {}",
                    request != null ? request.getConversationId() : null, ex.getMessage(), ex);
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
            log.debug("Persisting {} message for conversationId={} (latencyMs={})",
                    role, conversationId, latencyMs);
            messageService.createMessage(
                    conversationId,
                    CreateMessageRequest.builder()
                            .role(role)
                            .content(safeContent)
                            .latencyMs(latencyMs)
                            .build()
            );
        } catch (Exception ex) {
            log.error("Failed to persist {} message for conversation {}: {}",
                    role, conversationId, ex.getMessage(), ex);
        }
    }

    private ChatResponseDto buildResponse(DbChatState state, Long conversationId) {
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
            log.error("Error building chat response for conversationId={}: {}",
                    conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to build chat response: " + ex.getMessage());
        }
    }

    private ChatResponseDto.DebugInfo buildDebugInfo(DbChatState state) {
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
        } catch (Exception ex) {
            log.warn("Failed to build debug info for chat response: {}", ex.getMessage());
            return ChatResponseDto.DebugInfo.builder().build();
        }
    }
}