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
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.workflow.ChatWorkflow;
import semsem.chatbot.service.chat.LLMChattingService;
import semsem.chatbot.service.chat.MessageService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LLMChattingServiceImpl implements LLMChattingService {

    private final ChatWorkflow chatWorkflow;
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

            // Create initial state
            ChatGraphState initialState = ChatGraphState.fromQuery(conversationId, request.getMessage());

            // Execute workflow
            long startedAt = System.currentTimeMillis();
            ChatGraphState resultState = chatWorkflow.execute(initialState);
            long latencyMs = System.currentTimeMillis() - startedAt;

            // Persist the assistant's reply so the conversation history reloads.
            persistMessage(conversationId, MessageRole.ASSISTANT, resultState.getResponse(), latencyMs);

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

    private ChatResponseDto buildResponse(ChatGraphState state, Long conversationId) {
        try {
            ChatResponseDto.ChatResponseDtoBuilder builder = ChatResponseDto.builder()
                    .message(state.getResponse())
                    .conversationId(conversationId);

            // Add intent info
            Optional.ofNullable(state.getEntityExtractorOutput())
                    .map(QueryAnalyzerOutput::getIntent)
                    .ifPresent(intent -> {
                        builder.intent(intent.getName());
                        builder.intentConfidence(intent.getConfidence());
                    });

            // Add result info
            Optional.ofNullable(state.getSqlExecutorOutput())
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

    private ChatResponseDto.DebugInfo buildDebugInfo(ChatGraphState state) {
        try {
            ChatResponseDto.DebugInfo.DebugInfoBuilder debug = ChatResponseDto.DebugInfo.builder();

            Optional.ofNullable(state.getSqlGeneratorOutput())
                    .ifPresent(sql -> {
                        debug.generatedSql(sql.getGeneratedSql());
                        debug.sqlParameters(sql.getParameters());
                    });

            Optional.ofNullable(state.getLanguageDetectorOutput())
                    .ifPresent(lang -> debug.detectedLanguage(lang.getDetectedLanguage()));

            Optional.ofNullable(state.getEntityExtractorOutput())
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