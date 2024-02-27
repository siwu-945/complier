package Class;

import AST.ASTStatement;
import Expressions.Variable;

import java.util.ArrayList;

public class ClassMethod {
    String methodExp;
    ArrayList<Variable> localVar;
    ArrayList<ASTStatement> statements;
    String returnType;

    public ClassMethod(String methodExp, ArrayList<Variable> localVar, ArrayList<ASTStatement> statements, String returnType) {
        this.methodExp = methodExp;
        this.localVar = localVar;
        this.statements = statements;
        this.returnType = returnType;
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

    public String getReturnType() {
        return returnType;
    }

    public ArrayList<Variable> getLocalVar() {
        return localVar;
    }

}
