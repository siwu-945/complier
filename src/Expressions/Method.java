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

    public String argumentsString() {
        StringBuilder argumentsString = new StringBuilder();
        for (ASTExpression expression : arguments) {
            argumentsString.append(expression.toString());
            argumentsString.append("");
        }
        return argumentsString.toString();
    }

    @Override
    public String toString() {
        return object.toString() + "." + methodName + "(" + argumentsString() + ")";
    }

    public String currentClass() {
        return object.toString();
    }

    public String getMethodName() {
        return methodName;
    }

    public ASTExpression getObject() {
        return object;
    }
}
