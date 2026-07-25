package semsem.chatbot.tool.realestate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for calculating mortgage payments.
 */
@Slf4j
@Component
public class MortgageCalculatorTool extends BaseTool {

    public MortgageCalculatorTool() {
        this.name = "mortgage_calculator";
        this.description = "Calculate monthly mortgage payments based on loan amount, interest rate, and term";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "loanAmount", Map.of("type", "number", "description", "Total loan amount"),
                        "interestRate", Map.of("type", "number", "description", "Annual interest rate (percentage)"),
                        "termYears", Map.of("type", "integer", "description", "Loan term in years"),
                        "downPayment", Map.of("type", "number", "description", "Down payment amount")
                ),
                "required", new String[]{"loanAmount", "interestRate", "termYears"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement mortgage calculation
        double loanAmount = ((Number) arguments.get("loanAmount")).doubleValue();
        double interestRate = ((Number) arguments.get("interestRate")).doubleValue();
        int termYears = ((Number) arguments.get("termYears")).intValue();

        // Simple mortgage calculation
        double monthlyRate = interestRate / 100 / 12;
        int numPayments = termYears * 12;
        double monthlyPayment = loanAmount * (monthlyRate * Math.pow(1 + monthlyRate, numPayments))
                / (Math.pow(1 + monthlyRate, numPayments) - 1);

        return ToolResult.success(
                String.format("Monthly payment: $%.2f", monthlyPayment),
                Map.of("monthlyPayment", monthlyPayment, "totalPayment", monthlyPayment * numPayments)
        );
    }
}
