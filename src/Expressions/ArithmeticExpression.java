package Expressions;

import AST.ASTExpression;

public class ArithmeticExpression extends ASTExpression {
    ASTExpression left;
    ASTExpression right;
    String op;

    public ArithmeticExpression(ASTExpression left, String op, ASTExpression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {

        String result = left.toString() + " " + op + " " + right.toString();
        return result;
    }

    public ASTExpression getLeft() {
        return left;
    }

    public ASTExpression getRight() {
        return right;
    }

    public String getOp() {
        return op;
    }
}
