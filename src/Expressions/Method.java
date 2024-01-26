package Expressions;

import AST.ASTExpression;

import java.util.List;

public class Method extends ASTExpression {
    private final ASTExpression object;
    private final String methodName;
    private final List<ASTExpression> arguments;

    public Method(ASTExpression object, String methodName, List<ASTExpression> arguments) {
        this.object = object;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public StringBuilder argumentsString() {
        StringBuilder argumentsString = new StringBuilder();
        for (ASTExpression expression : arguments) {
            argumentsString.append(expression.toString());
        }
        return argumentsString;
    }

    @Override
    public String toString() {
        return object.toString() + methodName + "(" + argumentsString().toString() + ")";
    }
}
