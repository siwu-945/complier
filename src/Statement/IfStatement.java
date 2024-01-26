package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.List;

public class IfStatement extends ASTStatement {

    ASTExpression exp;
    List<ASTStatement> trueBranch;
    List<ASTStatement> falseBranch;

    public IfStatement(ASTExpression exp, List<ASTStatement> trueBranch, List<ASTStatement> falseBranch) {
        this.exp = exp;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

}
