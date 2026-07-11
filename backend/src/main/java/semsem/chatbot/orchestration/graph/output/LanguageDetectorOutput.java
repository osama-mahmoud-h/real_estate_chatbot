package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

/**
 * Output from LANGUAGE_DETECTOR node.
 */
@Data
@Builder
public class LanguageDetectorOutput {

    private String detectedLanguage;
    private String languageCode;
    private double confidence;
}
