package ControlTransfer;

import Primitives.IRVariable;

public class returnControl extends ControlTransfer {
    IRVariable returnVariable;

    public returnControl(IRVariable returnVariable) {
        this.returnVariable = returnVariable;
    }

    @Override
    public String toString() {
        return "ret " + returnVariable.toString();
    }
}
