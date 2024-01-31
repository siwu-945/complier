import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Primitives.IRStatement;
import Primitives.TransformIR;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        //TODO: fix this arth
        String code = "(3 + (10 + 3))";
//
        Parser myParser = new Parser();
        TransformIR myIR = new TransformIR();
        ArrayList<BasicBlock> myBlocks = new ArrayList<BasicBlock>();
        ArrayList<IRStatement> myIRStatements = new ArrayList<>();

//        ASTExpression myExp = myParser.parseExpr(code).getFirst();

//        System.out.println(myExp);

        String methods = "^object.myFunc(3, 4, 5)";
//        ASTExpression myMethods = myParser.parseExpr(methods).getFirst();
//        System.out.println(myMethods);

//        String myblock = "if (3 + 10): { \n" +
//                "print(3)\n" +
//                "}";

        String test = "x = (3 + (3 * 5))";
//
        String test2 = "x = ((3 + 3) * 5)";
//        ArrayList<ASTStatement> myStatements = myParser.parseStatementBlock(test);
//        for (ASTStatement stat : myStatements) {
//            System.out.println(stat);
//
//        }

        String irTest2 = "    x = (4 + 5)\n" +
                "    x = 73\n" +
                "    y = ((4 + 5) + 3)\n" +
                " print(3)";
        String irTest1 =
                "    y = ((4 + 5) + 3)\n" +
                        " print(3)";

//        myParser.parseExpr("3)");
        ArrayList<ASTStatement> codeToStas = myParser.parseStatementBlock(irTest1);
        myBlocks.add(new BasicBlock(myIRStatements, "initial"));

//        for (ASTStatement s : codeToStas) {
//            String myIRStatName = myIR.exprToIR(s, myBlocks, "initial");
//        }
        for (BasicBlock block : myBlocks) {
            for (IRStatement irLine : block.getIRStatements()) {
                System.out.println(irLine);
            }
        }

    }
}