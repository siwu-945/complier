package Types;

import AST.ASTExpression;
import Class.ClassNode;
import Expressions.Number;
import Expressions.*;
import Utility.SeperateVarInfo;

public class CheckExpressionType {

    public Type exprType(ASTExpression expr, TypeEnvironment typeEnv, ClassNode classNode) {
        if (expr instanceof Number) {
            return new IntType();
        }
        else if (expr instanceof Variable) {
            String variableName = ((Variable) expr).getName();
            return typeEnv.typeLookUp(variableName);
        }
        else if (expr instanceof ArithmeticExpression) {
            ArithmeticExpression arithExpr = (ArithmeticExpression) expr;
            Type leftType = exprType(arithExpr.getLeft(), typeEnv, classNode);
            Type rightType = exprType(arithExpr.getRight(), typeEnv, classNode);
            if (leftType == rightType) {
                return leftType;
            }
            else {
                return new ErrorType("Binary Operation Mismatch");
            }
        }
        else if (expr instanceof ClassExpr) {
            if (classNode == null) {
                String classExprName = ((ClassExpr) expr).getClassName();
                if (typeEnv.typeLookUp(classExprName) instanceof ClassType) {
                    return typeEnv.typeLookUp(classExprName);
                }
            }
            return new ClassType(classNode);
        }
        else if (expr instanceof thisRef) {
            return typeEnv.typeLookUp("this");
        }
        else if (expr instanceof Method) {
            //TODO: Implement method type checking
            ASTExpression object = ((Method) expr).getObject();
            //if object is a class object, check the method type
            if (exprType(object, typeEnv, classNode) instanceof ClassType) {
                String methodName = ((Method) expr).getMethodName();
                return typeEnv.typeLookUp(methodName);
            }
            else {
                return new ErrorType("Object mismatch");
            }
        }
        else if (expr instanceof FieldRead) {
            String classObjName = ((FieldRead) expr).getObj();
            ClassNode classInfo = ((ClassType) typeEnv.typeLookUp(classObjName)).getClassInfo();
            String fieldName = ((FieldRead) expr).getField();
//            Type fieldType = SeperateVarInfo.seperateType(((FieldRead) expr).getField());
            int fieldIndex = classInfo.getPureFieldNames().indexOf(fieldName);
            if (fieldIndex > -1) {
                Type fieldType = SeperateVarInfo.seperateType(classInfo.getFields().get(fieldIndex));
                return fieldType;
            }
        }
        return null;
    }
}
