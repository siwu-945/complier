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
    private String attribute;
    private ArrayList<BasicBlock> predecessors;

    public BasicBlock(ArrayList<IRStatement> IRStatements, String blockname, String attribute) {
        this.IRStatements = IRStatements;
        this.blockname = blockname;
        this.attribute = attribute;
        this.predecessors = new ArrayList<>();
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

    public String getAttribute() {
        return attribute;
    }

    public void addPredecessor(BasicBlock predecessor) {
        if (!predecessors.contains(predecessor)) {
            predecessors.add(predecessor);
        }
    }
}
