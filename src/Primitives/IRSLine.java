package Primitives;

public class IRSLine extends IRStatement {

    String statement;

    public IRSLine(String statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return statement;
    }
}
