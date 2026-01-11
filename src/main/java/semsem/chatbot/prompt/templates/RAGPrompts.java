package semsem.chatbot.prompt.templates;

/**
 * Prompt templates for RAG operations.
 */
public class RAGPrompts {

    public static final String CONTEXT_QA = """
            Answer the question based on the following context. If the answer cannot be found in the context, say "I don't have enough information to answer that question."

            Context:
            {{context}}

            Question: {{question}}

            Answer:""";

    public static final String CONTEXT_QA_WITH_SOURCES = """
            Answer the question based on the following context. Cite the relevant sources in your answer.

            Context:
            {{context}}

            Question: {{question}}

            Provide a detailed answer with source citations:""";

    public static final String DOCUMENT_SUMMARY = """
            Summarize the following document in a concise manner, highlighting the key points:

            Document:
            {{document}}

            Summary:""";

    public static final String QUERY_REWRITE = """
            Rewrite the following query to be more specific and searchable. Generate {{num_queries}} alternative queries.

            Original query: {{query}}

            Alternative queries:""";

    public static final String HYDE_GENERATION = """
            Generate a hypothetical document that would answer the following question:

            Question: {{question}}

            Hypothetical document:""";
}
