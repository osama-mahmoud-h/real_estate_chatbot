package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Output from ENTITY_EXTRACTOR node.
 */
@Data
@Builder
public class EntityExtractorOutput {

    private Map<String, Object> entities;
    private List<ExtractedEntity> extractedEntities;

    @Data
    @Builder
    public static class ExtractedEntity {
        private String type;
        private String value;
        private double confidence;
        private int startIndex;
        private int endIndex;
    }
}
