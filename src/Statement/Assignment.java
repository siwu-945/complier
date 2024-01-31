package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class Assignment extends ASTStatement {
    ASTExpression variable;
    ASTExpression e;

    public Assignment(ASTExpression variable, ASTExpression e) {
        this.variable = variable;
        this.e = e;
    }

    @Override
    public ASTExpression getVariable() {
        return variable;
    }

    @Override
    public ASTExpression getExpr() {
        return e;
    }

    @Override
    public String toString() {
        return variable.toString() + " = " + e.toString();
    }


}
