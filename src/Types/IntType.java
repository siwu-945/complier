package Types;


import Expressions.Number;

public class IntType extends Type {
    Number value;

    public IntType(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }
}
