import AST.ASTExpression;
import AST.ASTStatement;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        String code = "((3 + 10) + 3)";

        Parser myParser = new Parser();

        ASTExpression myExp = myParser.parseExpr(code).getFirst();

        System.out.println(myExp);

        String methods = "^object.myFunc(3, 4, 5)";
        ASTExpression myMethods = myParser.parseExpr(methods).getFirst();
        System.out.println(myMethods);

        String myblock = "if (1 = 1): { \n" +
                "print(3)\n" +
                "}";

        ArrayList<ASTStatement> myStatements = myParser.parseStatementBlock(myblock);


    }
}