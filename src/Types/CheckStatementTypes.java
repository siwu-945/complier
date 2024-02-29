package Types;

import AST.ASTStatement;
import Class.ClassNode;
import Expressions.Number;
import Statement.Assignment;
import Statement.FieldUpdate;
import Statement.IfStatement;
import Utility.SeperateVarInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckStatementTypes {

    //TODO: implement parameter type check
    public static boolean checkStatementTypes(ASTStatement statement, TypeEnvironment typeEnv, ClassNode newClass) {
        CheckExpressionType checkExpr = new CheckExpressionType();
        ErrorType typeMismatch = new ErrorType("Type mismatch");
        if (statement instanceof Assignment) {
            Assignment assignment = (Assignment) statement;
            String variableName = assignment.getVariable().toString();
            Type variableType = typeEnv.typeLookUp(variableName);
            Type exprType = checkExpr.exprType(assignment.getExpr(), typeEnv, newClass);

            if (exprType == null) {
                System.out.println("Expression doesn't exist");
                return false;
            }

            if (variableType.getClass() != exprType.getClass()) {
                ErrorType wrongAssignment = new ErrorType("Wrong Assignment");
                System.out.println(wrongAssignment);
                return false;
            }
        }
        else if (statement instanceof FieldUpdate) {
            FieldUpdate fieldUpdate = (FieldUpdate) statement;
            String variableName = fieldUpdate.getVariable().toString();
            if (variableName.startsWith("@")) {
                variableName = variableName.substring(1);
            }
            String fieldName = fieldUpdate.getField();
            Type variableType = typeEnv.typeLookUp(variableName);

            ClassNode classObj = ((ClassType) variableType).getClassInfo();
            ArrayList<String> fieldNames = classObj.getPureFieldNames();
            Type exprType = checkExpr.exprType(fieldUpdate.getExpr(), typeEnv, newClass);
            Type fieldType = null;
            //check if the field object actually has the field it is trying to access
            if (fieldNames.contains(fieldName)) {
                int fieldIndex = fieldNames.indexOf(fieldName);
                String fieldInfo = classObj.getFields().get(fieldIndex);
                fieldType = SeperateVarInfo.seperateType(fieldInfo);
            }

            boolean exprIs0 = false;
            if (fieldUpdate.getExpr() instanceof Number) {
                if (((Number) fieldUpdate.getExpr()).getValue() == 0) {
                    exprIs0 = true;
                }
            }

            if (fieldType == null) {
                System.out.println(new ErrorType("Invalid Field Access"));
                return false;
            }
            //TODO: double check
            if (fieldType.getClass() != exprType.getClass() && !exprIs0) {
                System.out.println(typeMismatch);
                return false;
            }
        }
        else if (statement instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) statement;
            Type conditionType = checkExpr.exprType(ifStatement.getExpr(), typeEnv, newClass);
            if (!(conditionType instanceof IntType)) {
                System.out.println(new ErrorType("Condition doesn't match"));
                return false;
            }
            for (ASTStatement sta : ifStatement.getTrueBranch()) {
                checkStatementTypes(sta, typeEnv, newClass);
            }

            for (ASTStatement sta : ifStatement.getFalseBranch()) {
                checkStatementTypes(sta, typeEnv, newClass);
            }
        }
        return true;
    }

    public static boolean checkField(TypeEnvironment typeEnv, String fieldName) {
        HashMap<String, Type> typeMap = typeEnv.getTypeEnv();
        for (Map.Entry<String, Type> entry : typeMap.entrySet()) {
            if (entry.getKey().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public void checkMethodTypes() {

    }
}
