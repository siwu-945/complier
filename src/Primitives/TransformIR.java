package Primitives;

import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Expressions.Variable;
import Statement.*;

import java.util.ArrayList;

public class TransformIR {
    int counter = 0;

    public String exprToIR(ASTStatement statement, ArrayList<BasicBlock> bs, String cur) {
        BasicBlock currentBlock = findBlockByName(bs, cur);
        String name = "x";
        if (statement instanceof Assignment) {
            IRStatement newIR = new IRAssignment(statement.getVariable().toString(), statement.getExpr());
            counter++;
            currentBlock.addIRStatement(newIR);
        } else if (statement instanceof WhileStatement) {
            return name;
        } else if (statement instanceof IfStatement) {
            return name;
        } else if (statement instanceof IfOnly) {
            return name;
        } else if (statement instanceof ReturnStatement) {
            return name;
        } else if (statement instanceof FieldUpdate) {
            return name;
        } else if (statement instanceof PrintStatement) {
            String printVal = statement.getVariable().toString();
            if (statement.getVariable() instanceof Variable) {
                printVal = "%" + statement.getVariable().toString();
                counter++;
            }
            IRStatement newIR = new IRPrint(printVal);
            currentBlock.addIRStatement(newIR);
        }
        return name;
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
