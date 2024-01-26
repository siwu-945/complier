package Expressions;

import AST.ASTExpression;

public class FieldRead extends ASTExpression {
    Object obj;
    String name;

    public FieldRead(Object obj, String name) {
        this.obj = obj;
        this.name = name;
    }

    @Override
    public String toString() {
        return obj.toString() + name;
    }
}
