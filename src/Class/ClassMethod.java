package Class;

import AST.ASTExpression;
import AST.ASTStatement;
import Expressions.Variable;

import java.util.ArrayList;

public class ClassMethod {
    ASTExpression methodExp;
    ArrayList<Variable> localVar;
    ArrayList<ASTStatement> statements;

    public ClassMethod(ASTExpression methodExp, ArrayList<Variable> localVar, ArrayList<ASTStatement> statements) {
        this.methodExp = methodExp;
        this.localVar = localVar;
        this.statements = statements;
    }

}
