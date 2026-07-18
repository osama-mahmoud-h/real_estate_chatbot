package semsem.chatbot.config;

import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrchestrationConfig {

    @Bean
    public BaseCheckpointSaver checkpointSaver() {
        return new MemorySaver();
    }
}