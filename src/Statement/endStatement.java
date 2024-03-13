package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class endStatement extends ASTStatement {

    @Override
    public String toString() {
        return "";
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
