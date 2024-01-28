package Statement;

import AST.ASTExpression;
import AST.ASTStatement;

public class PrintStatement extends ASTStatement {
    ASTExpression printVal;

    public PrintStatement(ASTExpression printVal) {
        this.printVal = printVal;
    }


}
