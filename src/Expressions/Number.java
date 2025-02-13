package Expressions;

import AST.ASTExpression;

public class Number extends ASTExpression {
    int value;

    public Number(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public int getValue() {
        return value;
    }
}
