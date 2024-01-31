package Primitives;

import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Statement.*;

import java.util.Set;

public class TransformIR {
    int counter = 0;

    public String exprToIR(ASTStatement statement, Set<BasicBlock> bs, String cur) {
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
        }
        return name;
    }

    private BasicBlock findBlockByName(Set<BasicBlock> bs, String cur) {
        for (BasicBlock b : bs) {
            if (b.getName().equals(cur)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Block not found: " + cur);
    }
}
