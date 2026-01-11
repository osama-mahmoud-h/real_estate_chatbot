package semsem.chatbot.chain;

import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Chain that executes multiple chains in parallel.
 * Results are merged into a single output.
 */
@Builder
public class ParallelChain extends BaseChain {

    private final List<Chain> chains;
    private final String mergeStrategy;

    @Override
    public String getName() {
        return "ParallelChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement parallel execution
        return ChainResult.success("");
    }
}
