package Primitives;

public class Operand extends IRStatement {
    String value;

    public Operand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
