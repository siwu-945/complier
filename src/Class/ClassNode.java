package Class;

import AST.ASTNode;
import Expressions.Variable;

import java.util.ArrayList;

public class ClassNode extends ASTNode {
    String className;
    ArrayList<Field> fields;
    ArrayList<ClassMethod> methods;
    ArrayList<Variable> localVar;


    public ClassNode(String className, ArrayList<Field> fields, ArrayList<ClassMethod> methods) {
        this.className = className;
        this.fields = fields;
        this.methods = methods;
    }


}
