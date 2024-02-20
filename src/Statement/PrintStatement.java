package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class PrintStatement extends ASTStatement {
    ASTExpression printVal;

    public PrintStatement(ASTExpression printVal) {
        this.printVal = printVal;
    }

    @Override
    public String toString() {
        return printVal.toString();
    }

    @Override
    public ASTExpression getVariable() {
        return printVal;
    }

    @Override
    public ASTExpression getExpr() {
        return printVal;
    }

}
