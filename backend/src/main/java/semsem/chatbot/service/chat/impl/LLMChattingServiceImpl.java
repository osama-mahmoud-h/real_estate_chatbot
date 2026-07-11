package semsem.chatbot.service.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semsem.chatbot.exception.ApiException;
import semsem.chatbot.model.dto.request.ChatRequestDto;
import semsem.chatbot.model.dto.response.ChatResponseDto;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.workflow.ChatWorkflow;
import semsem.chatbot.service.chat.LLMChattingService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LLMChattingServiceImpl implements LLMChattingService {

    private final ChatWorkflow chatWorkflow;

    @Value("${chatbot.debug:false}")
    private boolean debugMode;

    @Override
    public ChatResponseDto askLLM(ChatRequestDto request) {
        try {
            log.info("Received chat request: {}", request.getMessage());

            Long conversationId = Optional.ofNullable(request.getConversationId())
                    .filter(id -> !id.equals(null))
                    .orElseThrow(() -> new ApiException("Conversation ID is required"));

            // Create initial state
            ChatGraphState initialState = ChatGraphState.fromQuery(conversationId, request.getMessage());

            // Execute workflow
            ChatGraphState resultState = chatWorkflow.execute(initialState);

            // Build response
            return buildResponse(resultState, conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error processing chat request: {}", ex.getMessage(), ex);
            throw new ApiException("Failed to process chat request: " + ex.getMessage());
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