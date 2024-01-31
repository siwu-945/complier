package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.List;

public class WhileStatement extends ASTStatement {
    ASTExpression exp;
    List<ASTStatement> whileBranch;

    public WhileStatement(ASTExpression exp, List<ASTStatement> whileBranch) {
        this.exp = exp;
        this.whileBranch = whileBranch;
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
