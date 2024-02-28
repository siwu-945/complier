package Types;

public class ErrorType extends Type {
    String msg;

    public ErrorType(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ErrorType: " + msg;
    }
}
