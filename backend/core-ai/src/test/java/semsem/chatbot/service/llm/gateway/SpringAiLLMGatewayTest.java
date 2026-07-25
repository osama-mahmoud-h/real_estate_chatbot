package semsem.chatbot.service.llm.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpringAiLLMGateway} — the sync-text ({@link LLMGateway}) and
 * structured ({@link StructuredLLMGateway}) seams over Spring AI's {@link ChatModel}.
 */
@ExtendWith(MockitoExtension.class)
class SpringAiLLMGatewayTest {

    @Mock
    private ChatModel chatModel;

    private SpringAiLLMGateway gateway;

    private final Prompt prompt = new Prompt(List.of(new UserMessage("hi")));

    @BeforeEach
    void setUp() {
        gateway = new SpringAiLLMGateway(chatModel, new ObjectMapper());
    }

    private void stubReply(String text) {
        when(chatModel.call(prompt))
                .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(text)))));
    }

    @Test
    void invoke_returnsAssistantText() {
        stubReply("hello world");
        assertThat(gateway.invoke(prompt)).isEqualTo("hello world");
    }

    @Test
    void invoke_returnsEmptyStringWhenModelFails() {
        when(chatModel.call(prompt)).thenThrow(new RuntimeException("boom"));
        assertThat(gateway.invoke(prompt)).isEmpty();
    }

    @Test
    void invoke_returnsEmptyStringOnNullResponse() {
        when(chatModel.call(prompt)).thenReturn(null);
        assertThat(gateway.invoke(prompt)).isEmpty();
    }

    @Test
    void invokeStructured_parsesFencedJsonIntoType() {
        stubReply("```json\n{\"city\":\"Cairo\",\"temp\":40}\n```");

        Weather weather = gateway.invokeStructured(prompt, Weather.class);

        assertThat(weather.city).isEqualTo("Cairo");
        assertThat(weather.temp).isEqualTo(40);
    }

    @Test
    void invokeStructured_parsesUnclosedJsonFence() {
        stubReply("```json\n{\"city\":\"Cairo\",\"temp\":40}"); // opener, no closing fence

        Weather weather = gateway.invokeStructured(prompt, Weather.class);

        assertThat(weather.city).isEqualTo("Cairo");
        assertThat(weather.temp).isEqualTo(40);
    }

    @Test
    void invokeStructured_throwsWhenModelReturnsNothing() {
        when(chatModel.call(prompt)).thenReturn(null); // invoke() -> ""

        assertThatThrownBy(() -> gateway.invokeStructured(prompt, Weather.class))
                .isInstanceOf(IllegalStateException.class);
    }

    /** Minimal JSON target for the structured-parse test. */
    static class Weather {
        public String city;
        public int temp;
    }
}