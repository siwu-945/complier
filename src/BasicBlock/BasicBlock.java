package BasicBlock;

import Primitives.IRStatement;

import java.util.ArrayList;
import java.util.HashMap;
//Ends with either
//        • A return statement, or
//        • An unconditional jump, or
//        • A conditional check which determines the next block to execute

public class BasicBlock {
    private ArrayList<IRStatement> IRStatements;
    private String blockname;
    private String attribute;
    private ArrayList<BasicBlock> predecessors;
    private HashMap<String, ArrayList<String>> variableDefined;

    public BasicBlock(ArrayList<IRStatement> IRStatements, String blockname, String attribute) {
        this.IRStatements = IRStatements;
        this.blockname = blockname;
        this.attribute = attribute;
        this.predecessors = new ArrayList<>();
        this.variableDefined = new HashMap<>();
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

    public boolean hasMultiplePredecessors() {
        return predecessors.size() > 1;
    }

    public ArrayList<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public HashMap<String, ArrayList<String>> getVariableDefined() {
        return variableDefined;
    }

    public void addVariableDefined(String variable, String tmpVar) {
        if (variableDefined.containsKey(variable)) {
            variableDefined.get(variable).add(tmpVar);
        }
        else {
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(tmpVar);
            variableDefined.put(variable, tmp);
        }
    }
}
