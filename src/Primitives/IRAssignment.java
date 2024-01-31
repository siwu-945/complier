package Primitives;

import AST.ASTExpression;

public class IRAssignment extends IRStatement {

    String variable;
    ASTExpression right;

    public IRAssignment(String variable, ASTExpression right) {
        this.variable = variable;
        this.right = right;
    }

}
