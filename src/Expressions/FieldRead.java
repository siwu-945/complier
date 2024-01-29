package Expressions;

import AST.ASTExpression;

public class FieldRead extends ASTExpression {
    ASTExpression obj;
    String name;

    public FieldRead(ASTExpression obj, String name) {
        this.obj = obj;
        this.name = name;
    }

    @Override
    public String toString() {
        return obj.toString() + name;
    }
}
