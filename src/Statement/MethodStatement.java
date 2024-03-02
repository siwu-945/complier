package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.List;

public class MethodStatement extends ASTStatement {
    private final ASTExpression object;
    private final String methodName;
    private final List<ASTExpression> arguments;

    public MethodStatement(ASTExpression object, String methodName, List<ASTExpression> arguments) {
        this.object = object;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String argumentsString() {
        StringBuilder argumentsString = new StringBuilder();
        int count = 1;
        for (ASTExpression expression : arguments) {
            argumentsString.append(expression.toString());
            if (count < arguments.size()) {
                argumentsString.append(", ");
            }
            count++;
        }
        return argumentsString.toString();
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return object;
    }
}
