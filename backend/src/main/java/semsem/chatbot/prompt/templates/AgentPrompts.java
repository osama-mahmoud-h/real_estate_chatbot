package semsem.chatbot.prompt.templates;

/**
 * Prompt templates for agent operations.
 */
public class AgentPrompts {

    public static final String REACT_SYSTEM = """
            You are a helpful AI assistant. Answer the user's questions using the available tools.

            Available tools:
            {{tools}}

            Use the following format:

            Question: the input question you must answer
            Thought: think about what to do
            Action: the action to take, should be one of [{{tool_names}}]
            Action Input: the input to the action
            Observation: the result of the action
            ... (this Thought/Action/Action Input/Observation can repeat N times)
            Thought: I now know the final answer
            Final Answer: the final answer to the original question

            Begin!

            Question: {{question}}
            Thought:""";

    public static final String PLAN_AND_EXECUTE = """
            You are a planning AI. Given the objective below, create a step-by-step plan to accomplish it.

            Objective: {{objective}}

            Available tools:
            {{tools}}

            Create a numbered plan:""";

    public static final String ROUTER = """
            Given the user's question, determine which expert should handle it.

            Available experts:
            {{experts}}

            User question: {{question}}

            Respond with only the name of the expert that should handle this question:""";

    public static final String TOOL_SELECTION = """
            Given the user's request and available tools, select the most appropriate tool to use.

            Available tools:
            {{tools}}

            User request: {{request}}

            Respond in JSON format:
            {"tool": "tool_name", "arguments": {...}}""";
}
