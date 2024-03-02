package Types;

import AST.ASTExpression;
import Class.ClassMethod;
import Class.ClassNode;
import Expressions.Number;
import Expressions.Object;
import Expressions.*;
import Utility.SeperateVarInfo;
import Utility.StringToType;

import java.util.ArrayList;

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
                return null;
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
                Type classType = typeEnv.typeLookUp(object.toString());
                ClassNode classInfo = ((ClassType) classType).getClassInfo();
                ArrayList<ClassMethod> classMethods = classInfo.getMethods();
                for (ClassMethod method : classMethods) {
                    if (method.getMethodName().equals(methodName)) {
                        String returnType = method.getReturnType();
                        return StringToType.toType(method.getReturnType(), classInfo);
                    }
                }

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
        else if (expr instanceof equality) {
            ASTExpression leftExpr = ((equality) expr).getLeft();
            ASTExpression rightExpr = ((equality) expr).getRight();

            if (rightExpr instanceof Number) {
                if (((Number) rightExpr).getValue() == 0) {
                    return new IntType();
                }
            }
            Type leftType = exprType(leftExpr, typeEnv, classNode);
            Type rightType = exprType(rightExpr, typeEnv, classNode);

            if (leftType.getClass() != rightType.getClass()) {
                return new ErrorType("invalid condition");
            }

        }
        else if (expr instanceof Object && typeEnv.hasClass(expr.toString())) {
            return typeEnv.typeLookUp(expr.toString());
        }
        return null;
    }
}
