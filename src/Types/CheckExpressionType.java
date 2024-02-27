package Types;

import AST.ASTExpression;
import Expressions.Number;
import Expressions.*;

public class CheckExpressionType {

    public Type exprType(ASTExpression expr, TypeEnvironment typeEnv) {
        if (expr instanceof Number) {
            return new IntType();
        }
        else if (expr instanceof Variable) {
            String variableName = ((Variable) expr).getName();
            return typeEnv.typeLookUp(variableName);
        }
        else if (expr instanceof ArithmeticExpression) {
            ArithmeticExpression arithExpr = (ArithmeticExpression) expr;
            Type leftType = exprType(arithExpr.getLeft(), typeEnv);
            Type rightType = exprType(arithExpr.getRight(), typeEnv);
            if (leftType == rightType) {
                return leftType;
            }
            else {
                return new ErrorType("Binary Operation Mismatch");
            }
        }
        else if (expr instanceof ClassExpr) {
            return new ClassType(((ClassExpr) expr).getClassName());
        }
        else if (expr instanceof thisRef) {
            return typeEnv.typeLookUp("this");
        }
        else if (expr instanceof Method) {
            //TODO: Implement method type checking
            ASTExpression object = ((Method) expr).getObject();
            //if object is a class object, check the method type
            if (exprType(object, typeEnv) instanceof ClassType) {
                String methodName = ((Method) expr).getMethodName();
                return typeEnv.typeLookUp(methodName);
            }
            else {
                return new ErrorType("Object mismatch");
            }
        }
        return null;
    }
}
