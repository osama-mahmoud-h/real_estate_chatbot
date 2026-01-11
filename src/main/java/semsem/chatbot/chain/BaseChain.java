package semsem.chatbot.chain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Abstract base class for chains with common functionality.
 */
@Getter
@Setter
public abstract class BaseChain implements Chain {

    protected String name;
    protected Object memory;
    protected Object callbacks;

    @Override
    public Chain withMemory(Object memory) {
        this.memory = memory;
        return this;
    }

    @Override
    public Chain withCallbacks(Object callbacks) {
        this.callbacks = callbacks;
        return this;
    }

    protected abstract ChainResult doInvoke(Map<String, Object> inputs);

    @Override
    public ChainResult invoke(Map<String, Object> inputs) {
        long startTime = System.currentTimeMillis();
        try {
            ChainResult result = doInvoke(inputs);
            result.setLatencyMs(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            return ChainResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .latencyMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
