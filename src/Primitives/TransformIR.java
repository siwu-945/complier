package Primitives;

import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Expressions.ArithmeticExpression;
import Statement.Assignment;

import java.util.ArrayList;

public class TransformIR {
    int counter = 0;

//    public String StatementToIR(ASTStatement statement, ArrayList<BasicBlock> bs, String cur) {
//        BasicBlock currentBlock = findBlockByName(bs, cur);
//        String name = "x";
//        if (statement instanceof Assignment) {
////            if(statement.getExpr() instanceof ArithmeticExpression){
////
////            }
//            IRVariable variableNode = new IRVariable(statement.getVariable().toString());
//            IRStatement newIR = new IRAssignment(variableNode, statement.getExpr());
//            counter++;
//            currentBlock.addIRStatement(newIR);
//        } else if (statement instanceof WhileStatement) {
//            return name;
//        } else if (statement instanceof IfStatement) {
//            return name;
//        } else if (statement instanceof IfOnly) {
//            return name;
//        } else if (statement instanceof ReturnStatement) {
//            return name;
//        } else if (statement instanceof FieldUpdate) {
//            return name;
//        } else if (statement instanceof PrintStatement) {
//            String printVal = statement.getVariable().toString();
//            if (statement.getVariable() instanceof Variable) {
//                printVal = "%" + statement.getVariable().toString();
//                counter++;
//            }
//            IRStatement newIR = new IRPrint(printVal);
//            currentBlock.addIRStatement(newIR);
//        }
//        return name;
//    }

    public String exprToIR(ASTExpression expr, BasicBlock currentBlock) {
        int tmpVar = 1;
        if (expr instanceof ArithmeticExpression) {
            ((ArithmeticExpression) expr).getLeft();
            IRVariable tmp = new IRVariable(Integer.toString(tmpVar));
        }
        return "2";
    }

    public void addToBB(ArrayList<ASTStatement> statements, ArrayList<BasicBlock> bs, String cur) {
        BasicBlock currentBlock = findBlockByName(bs, cur);

        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                currentBlock.addIRStatement(newIR);
            }
        }
    }


    private BasicBlock findBlockByName(ArrayList<BasicBlock> bs, String cur) {
        for (BasicBlock b : bs) {
            if (b.getName().equals(cur)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Block not found: " + cur);
    }
}
