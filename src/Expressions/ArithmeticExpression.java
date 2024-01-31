package Expressions;

import AST.ASTExpression;

public class ArithmeticExpression extends ASTExpression {
    ASTExpression left;
    ASTExpression right;
    Character op;
    int value;

    public ArithmeticExpression(ASTExpression left, Character op, ASTExpression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public static int calculate(int a, int b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + op);
        }
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
}
