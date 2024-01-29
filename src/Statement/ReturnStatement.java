package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class ReturnStatement extends ASTStatement {

    ASTExpression returnExp;

    public ReturnStatement(ASTExpression returnExp) {
        this.returnExp = returnExp;
    }

}
