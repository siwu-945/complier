package Primitives;

import java.util.ArrayList;

public class phi extends IRStatement {
    ArrayList<String> blockNames;
    ArrayList<String> variables;
    IRVariable phiVar;

    public phi(IRVariable phiVar, ArrayList<String> blockNames, ArrayList<String> variables) {
        this.blockNames = blockNames;
        this.variables = variables;
        this.phiVar = phiVar;
    }

    public String argumentString() {
        String argString = "";
        for (int i = 0; i < blockNames.size(); i++) {
            argString += blockNames.get(i) + ", " + variables.get(i);
            if (i != blockNames.size() - 1) {
                argString += ", ";
            }
        }
        return argString;
    }

    @Override
    public String toString() {
        String statement = phiVar.toString() + " = phi(" + argumentString() + ")";
        return statement;
    }

}
