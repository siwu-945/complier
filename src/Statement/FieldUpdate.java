package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class FieldUpdate extends ASTStatement {
    ASTExpression e;
    String field;

    public FieldUpdate(ASTExpression e, String field) {
        this.e = e;
        this.field = field;
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return null;
    }
}
