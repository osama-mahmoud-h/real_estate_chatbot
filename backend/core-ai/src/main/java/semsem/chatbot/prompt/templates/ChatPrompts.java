package semsem.chatbot.prompt.templates;

/**
 * Prompt templates for chat operations.
 */
public class ChatPrompts {

    public static final String SYSTEM_DEFAULT = """
            You are a helpful AI assistant for a real estate company. You help users find properties,
            schedule viewings, answer questions about real estate, and provide market insights.

            Be friendly, professional, and helpful. If you don't know something, say so.
            Always prioritize user safety and privacy.""";

    public static final String SYSTEM_WITH_CONTEXT = """
            You are a helpful AI assistant for a real estate company.

            Conversation context:
            {{context}}

            Previous summary:
            {{summary}}

            Respond helpfully to the user's message.""";

    public static final String CONVERSATION_SUMMARY = """
            Summarize the following conversation in a few sentences, capturing the key points and user preferences:

            {{conversation}}

            Summary:""";

    public static final String INTENT_CLASSIFICATION = """
            Classify the user's intent from the following message. Choose from:
            - property_search: Looking for properties
            - schedule_viewing: Wants to schedule a viewing
            - mortgage_info: Asking about mortgages or financing
            - neighborhood_info: Asking about neighborhoods or areas
            - general_question: General real estate questions
            - greeting: Greeting or small talk
            - other: Other intent

            User message: {{message}}

            Intent:""";
}
