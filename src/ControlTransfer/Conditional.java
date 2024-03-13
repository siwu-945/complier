package ControlTransfer;

import Primitives.IRVariable;

public class Conditional extends ControlTransfer {
    private String trueBlock;
    private String falseBlock;
    private IRVariable variable;

    public Conditional(String trueBlock, String falseBlock, IRVariable variable) {
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
        this.variable = variable;
    }

    @Override
    public String toString() {
        if (falseBlock.equals("null")) {
            return "if " + variable.toString() + " then " + trueBlock;
        }
        return "if " + variable.toString() + " then " + trueBlock + " else " + falseBlock;
    }
}
