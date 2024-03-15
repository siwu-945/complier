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
        for (ASTExpression expression : arguments) {
            argumentsString.append(expression.toString());
            argumentsString.append("");
        }
        return argumentsString.toString();
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return object;
    }

    public String getMethodName() {
        return methodName;
    }
}
