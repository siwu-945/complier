package Primitives;

public class BinaryOperation extends IRStatement {
    String resultVar;
    Operand left;
    Operand right;
    String operator;

    public BinaryOperation(String resultVar, Operand left, Operand right, String operator) {
        this.resultVar = resultVar;
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
