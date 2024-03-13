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
        return returnExp;
    }

    @Override
    public ASTExpression getExpr() {
        return returnExp;
    }

}
