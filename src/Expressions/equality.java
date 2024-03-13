package Expressions;

import AST.ASTExpression;

public class equality extends ASTExpression {
    private ASTExpression left;
    private ASTExpression right;

    public equality(ASTExpression left, ASTExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " == " + right.toString();
    }

    public ASTExpression getLeft() {
        return left;
    }

    public ASTExpression getRight() {
        return right;
    }
}
