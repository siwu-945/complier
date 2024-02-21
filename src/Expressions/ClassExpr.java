package Expressions;

import AST.ASTExpression;

public class ClassExpr extends ASTExpression {
    String className;

    public ClassExpr(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "@" + className;
    }

    public String getClassName() {
        return className;
    }
}
