package Primitives;

import BasicBlock.BasicBlock;

import java.util.ArrayList;

public class AddPhiFunc {
    public void addPhi(BasicBlock block) {
        if (block.hasMultiplePredecessors()) {
            ArrayList<BasicBlock> predecessors = block.getPredecessors();
            ArrayList<String> blockNames = new ArrayList<>();
            for (BasicBlock b : predecessors) {
                blockNames.add(b.getName());
            }
        }
    }
}
