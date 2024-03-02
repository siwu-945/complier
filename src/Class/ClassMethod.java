package Class;

import AST.ASTStatement;
import Expressions.Variable;

import java.util.ArrayList;

public class ClassMethod {
    String methodExp;
    ArrayList<Variable> localVar;
    ArrayList<ASTStatement> statements;
    String returnType;
    String arguments;

    public ClassMethod(String methodExp, ArrayList<Variable> localVar, ArrayList<ASTStatement> statements, String returnType, String arguments) {
        this.methodExp = methodExp;
        this.localVar = localVar;
        this.statements = statements;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        String statementString = "";
        statementString += methodExp + ": ";
        for (Variable var : localVar) {
            statementString += var.toString() + " ";
        }
        statementString += "\n";
        for (ASTStatement statement : statements) {
            statementString += statement + "\n";
        }
        return statementString;
    }

    public String getMethodName() {
        return methodExp;
    }

    public ArrayList<ASTStatement> getStatements() {
        return statements;
    }

    public ArrayList<String> getArguments() {
        ArrayList<String> args = new ArrayList<>();
        String[] arg = arguments.split(",");
        for (String a : arg) {
            args.add(a);
        }
        return args;
    }

    public String getReturnType() {
        return returnType;
    }

    public ArrayList<Variable> getLocalVar() {
        return localVar;
    }

}
