package semsem.chatbot.chain;

import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Chain that executes multiple chains in sequence.
 * Output of each chain is passed as input to the next.
 */
@Builder
public class SequentialChain extends BaseChain {

    private final List<Chain> chains;
    private final List<String> inputVariables;
    private final List<String> outputVariables;

    @Override
    public String getName() {
        return "SequentialChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement sequential execution
        return ChainResult.success("");
    }
}
