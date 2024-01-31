package Primitives;

public class IRPrint extends IRStatement {

    String value;

    public IRPrint(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "print" + "(" + value + ")";
    }
}
