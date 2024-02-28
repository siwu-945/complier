package Class;

import AST.ASTExpression;
import AST.ASTStatement;
import Expressions.Variable;
import Utility.SeperateVarInfo;

import java.util.ArrayList;

public class ClassNode extends ASTStatement {
    String className;
    ArrayList<String> fields;
    ArrayList<ClassMethod> methods;
    ArrayList<Variable> localVar;
    ArrayList<String> fieldName;


    public ClassNode(String className, ArrayList<String> fields, ArrayList<ClassMethod> methods) {
        this.className = className;
        this.fields = fields;
        this.fieldName = translateFieldName(fields);
        this.methods = methods;
    }

    public ArrayList<String> translateFieldName(ArrayList<String> fieldsInfo) {
        ArrayList<String> fieldInfo = new ArrayList<>();
        for (String field : fieldsInfo) {
            String fieldName = SeperateVarInfo.seperateName(field);
            fieldInfo.add(fieldName);
        }
        return fieldInfo;
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<ClassMethod> getMethods() {
        return methods;
    }

    @Override
    public ASTExpression getVariable() {
        return null;
    }

    @Override
    public ASTExpression getExpr() {
        return null;
    }

    private String filedsString() {
        String fieldString = "";
        for (String field : fields) {
            fieldString += field;
        }
        return fieldString;
    }

    private String methodString() {
        String methodString = "";
        for (ClassMethod method : methods) {
            methodString += method.toString();
        }
        return methodString;
    }

    @Override
    public String toString() {
        String filedsString = filedsString();
        String methodString = methodString();
        return "class: " + className + "\n" + "[ " + "fields : " + filedsString + "\n" + methodString + " ]";
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public ArrayList<String> getPureFieldNames() {
        return fieldName;
    }

    public ArrayList<String> getMethodsNames() {
        ArrayList<String> methodNames = new ArrayList<>();

        for (ClassMethod method : methods) {
            methodNames.add(method.getMethodName());
        }
        return methodNames;
    }

    public void changeClassName() {
        this.className = this.className + "(this)";
    }
}
