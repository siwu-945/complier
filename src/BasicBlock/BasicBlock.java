package BasicBlock;

import Primitives.IRStatement;

import java.util.ArrayList;
//Ends with either
//        • A return statement, or
//        • An unconditional jump, or
//        • A conditional check which determines the next block to execute

public class BasicBlock {
    private ArrayList<IRStatement> IRStatements;
    private String blockname;
    private int tmpVar;

    public BasicBlock(ArrayList<IRStatement> IRStatements, String blockname) {
        this.IRStatements = IRStatements;
        this.blockname = blockname;
        this.tmpVar = 0;
    }

    public void addIRStatement(IRStatement irLine) {
        IRStatements.add(irLine);
    }

    public String getName() {
        return blockname;
    }

    @Override
    public String toString() {
        int index = 0;
        String lines = "";
        for (IRStatement statement : IRStatements) {
            String varName = "x" + Integer.toString(index);
            index++;
            lines += varName + " = " + statement.toString();
        }
        return lines;
    }

    public ArrayList<IRStatement> getIRStatements() {
        return IRStatements;
    }

    public int getTmpVar() {
        return tmpVar;
    }

    public void incrementTmpVar() {
        tmpVar++;
    }
}
