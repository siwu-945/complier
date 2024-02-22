package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.ArrayList;

public class WhileStatement extends ASTStatement {
    ASTExpression exp;
    ArrayList<ASTStatement> whileBranch;

    public WhileStatement(ASTExpression exp, ArrayList<ASTStatement> whileBranch) {
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

    public ArrayList<ASTStatement> getWhileBranch() {
        return whileBranch;
    }
}
