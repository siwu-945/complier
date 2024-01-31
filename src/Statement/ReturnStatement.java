package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class ReturnStatement extends ASTStatement {

    ASTExpression returnExp;

    public ReturnStatement(ASTExpression returnExp) {
        this.returnExp = returnExp;
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
