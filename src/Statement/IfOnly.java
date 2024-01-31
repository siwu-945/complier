package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.List;

public class IfOnly extends ASTStatement {
    ASTExpression exp;
    List<ASTStatement> trueBranch;

    public IfOnly(ASTExpression exp, List<ASTStatement> trueBranch) {
        this.exp = exp;
        this.trueBranch = trueBranch;
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
