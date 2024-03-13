package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

import java.util.ArrayList;

public class IfStatement extends ASTStatement {

    ASTExpression exp;
    ArrayList<ASTStatement> trueBranch;
    ArrayList<ASTStatement> falseBranch;

    public IfStatement(ASTExpression exp, ArrayList<ASTStatement> trueBranch, ArrayList<ASTStatement> falseBranch) {
        this.exp = exp;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public String toString() {
        String ifStrings = exp.toString() + "\n";
        for (ASTStatement sta : trueBranch) {
            ifStrings += sta;
        }
        return ifStrings;
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return exp;
    }

    public ArrayList<ASTStatement> getFalseBranch() {
        return falseBranch;
    }

    public ArrayList<ASTStatement> getTrueBranch() {
        return trueBranch;
    }
}
