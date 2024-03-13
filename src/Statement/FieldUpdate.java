package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class FieldUpdate extends ASTStatement {
    ASTExpression left_e;
    String field;
    ASTExpression right_e;

    public FieldUpdate(ASTExpression left_e, String field, ASTExpression right_e) {
        this.left_e = left_e;
        this.field = field;
        this.right_e = right_e;
    }

    @Override
    public ASTExpression getVariable() {
        return left_e;
    }

    @Override
    public ASTExpression getExpr() {
        return right_e;
    }

    public String getField() {
        return field;
    }
}
