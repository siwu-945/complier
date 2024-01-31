import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Primitives.IRStatement;
import Primitives.TransformIR;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        String code = "((3 + 10) + 3)";
//
        Parser myParser = new Parser();
        TransformIR myIR = new TransformIR();
        ArrayList<BasicBlock> myBlocks = new ArrayList<BasicBlock>();
        ArrayList<IRStatement> myIRStatements = new ArrayList<>();

        ASTExpression myExp = myParser.parseExpr(code).getFirst();

//        System.out.println(myExp);

        String methods = "^object.myFunc(3, 4, 5)";
//        ASTExpression myMethods = myParser.parseExpr(methods).getFirst();
//        System.out.println(myMethods);

//        String myblock = "if (3 + 10): { \n" +
//                "print(3)\n" +
//                "}";

//        String myBlock = "x = (3 + (3 * 5))";
//
//        ArrayList<ASTStatement> myStatements = myParser.parseStatementBlock(myBlock);
//        System.out.println(myStatements);

        String irTest1 = "    x = (4 + 5)\n" +
                "    x = 73\n" +
                "    y = (4 + 5)\n" +
                " print(3)";
        ArrayList<ASTStatement> codeToStas = myParser.parseStatementBlock(irTest1);
        myBlocks.add(new BasicBlock(myIRStatements, "initial"));

        for (ASTStatement s : codeToStas) {
            String myIRStatName = myIR.exprToIR(s, myBlocks, "initial");
        }
        for (BasicBlock block : myBlocks) {
            for (IRStatement irLine : block.getIRStatements()) {
                System.out.println(irLine);
            }
        }

    }
}