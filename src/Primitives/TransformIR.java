package Primitives;

import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Expressions.ArithmeticExpression;
import Expressions.Number;
import Statement.Assignment;

import java.util.ArrayList;

public class TransformIR {
    int tmpVar = 0;

    public String exprToIR(ASTExpression expr, BasicBlock currentBlock) {

        if (expr instanceof ArithmeticExpression) {

            String leftVar = exprToIR(((ArithmeticExpression) expr).getLeft(), currentBlock);
            String rightVar = exprToIR(((ArithmeticExpression) expr).getRight(), currentBlock);
            Character op = ((ArithmeticExpression) expr).getOp();
            tmpVar++;
            IRVariable newVar = new IRVariable(Integer.toString(tmpVar));
            IRAssignment newIR = new IRAssignment(newVar, leftVar + " " + op + " " + rightVar);
            currentBlock.addIRStatement(newIR);
        } else if (expr instanceof Number) {
            tmpVar++;
            String tmpName = Integer.toString(tmpVar);
            IRVariable newVar = new IRVariable(tmpName);
            IRAssignment newAssign = new IRAssignment(newVar, expr.toString());
            currentBlock.addIRStatement(newAssign);
        }

        return "%" + Integer.toString(tmpVar);
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
