package Expressions;

public class Pair<ASTExpression, String> {
    public final ASTExpression first;
    public final String second;

    public Pair(ASTExpression first, String second) {
        this.first = first;
        this.second = second;
    }

    public ASTExpression getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}
