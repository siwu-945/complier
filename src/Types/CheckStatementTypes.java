package Types;

import AST.ASTStatement;
import Statement.Assignment;
import Statement.FieldUpdate;
import Statement.IfStatement;

import java.util.HashMap;
import java.util.Map;

public class CheckStatementTypes {

    //TODO: is this the correct way of checking field access?
    public void checkStatementTypes(ASTStatement statement, TypeEnvironment typeEnv) {
        CheckExpressionType checkExpr = new CheckExpressionType();
        ErrorType typeMismatch = new ErrorType("Type mismatch");
        if (statement instanceof Assignment) {
            Assignment assignment = (Assignment) statement;
            String variableName = assignment.getVariable().toString();
            Type variableType = typeEnv.typeLookUp(variableName);
            Type exprType = checkExpr.exprType(assignment.getExpr(), typeEnv);
            if (variableType != exprType) {
                System.out.println(typeMismatch);
            }
        }
        else if (statement instanceof FieldUpdate) {
            FieldUpdate fieldUpdate = (FieldUpdate) statement;
            String variableName = fieldUpdate.getVariable().toString();
            String fieldName = fieldUpdate.getField();
            Type variableType = typeEnv.typeLookUp(variableName);
            boolean validFieldAccess = checkField(typeEnv, fieldName);
            Type exprType = checkExpr.exprType(fieldUpdate.getExpr(), typeEnv);

            if (!validFieldAccess) {
                System.out.println(new ErrorType("Invalid Field Access"));
            }
            if (variableType != exprType) {
                System.out.println(typeMismatch);
            }
        }
        else if (statement instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) statement;
            Type conditionType = checkExpr.exprType(ifStatement.getExpr(), typeEnv);
            if (!(conditionType instanceof IntType)) {
                System.out.println(new ErrorType("Condition doesn't match"));
            }
            for (ASTStatement sta : ifStatement.getTrueBranch()) {
                checkStatementTypes(sta, typeEnv);
            }

            for (ASTStatement sta : ifStatement.getFalseBranch()) {
                checkStatementTypes(sta, typeEnv);
            }
        }
    }

    public boolean checkField(TypeEnvironment typeEnv, String fieldName) {
        HashMap<String, Type> typeMap = typeEnv.getTypeEnv();
        for (Map.Entry<String, Type> entry : typeMap.entrySet()) {
            if (entry.getKey().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}
