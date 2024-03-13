package Expressions;

import AST.ASTExpression;

public class Variable extends ASTExpression {
    String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

}
