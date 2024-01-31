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

    public BasicBlock(ArrayList<IRStatement> IRStatements, String blockname) {

        this.IRStatements = IRStatements;
        this.blockname = blockname;
    }

    public void addIRStatement(IRStatement irLine) {
        IRStatements.add(irLine);
    }

    public String getName() {
        return blockname;
    }
}
