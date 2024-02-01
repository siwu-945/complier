package Class;

import AST.ASTExpression;
import AST.ASTStatement;
import Expressions.Variable;

import java.util.ArrayList;

public class ClassNode extends ASTStatement {
    String className;
    ArrayList<Field> fields;
    ArrayList<ClassMethod> methods;
    ArrayList<Variable> localVar;


    public ClassNode(String className, ArrayList<Field> fields, ArrayList<ClassMethod> methods) {
        this.className = className;
        this.fields = fields;
        this.methods = methods;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return null;
    }
}
