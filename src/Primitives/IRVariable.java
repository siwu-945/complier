package Primitives;

public class IRVariable extends IRStatement {

    String variableName;

    public IRVariable(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String toString() {
        return variableName;
    }
}
