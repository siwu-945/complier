package Expressions;

import AST.ASTExpression;

public class Variable extends ASTExpression {
    String name;

    public Variable(String name) {
        this.name = name;
    }
}
