package Primitives;

import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import ControlTransfer.Conditional;
import Expressions.ArithmeticExpression;
import Expressions.ClassExpr;
import Expressions.Number;
import Statement.Assignment;
import Statement.FieldUpdate;

import java.util.ArrayList;

public class TransformIR {
    int tmpVar = 0;
    int labelInt = 1;
    int classInt = -1;
    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    int classNum = 0;

    public String exprToIR(ASTExpression expr, BasicBlock currentBlock) {

        if (expr instanceof ArithmeticExpression) {
            String leftVar = exprToIR(((ArithmeticExpression) expr).getLeft(), currentBlock);
            String rightVar = exprToIR(((ArithmeticExpression) expr).getRight(), currentBlock);
            Character op = ((ArithmeticExpression) expr).getOp();
            tmpVar++;
            IRVariable newVar = new IRVariable("%" + Integer.toString(tmpVar));
            IRAssignment newIR = new IRAssignment(newVar, leftVar + " " + op + " " + rightVar);
            currentBlock.addIRStatement(newIR);
        } else if (expr instanceof Number) {
            tmpVar++;
            String tmpName = "%" + Integer.toString(tmpVar);
            IRVariable newVar = new IRVariable(tmpName);
            IRAssignment newAssign = new IRAssignment(newVar, expr.toString());
            currentBlock.addIRStatement(newAssign);
        }
        //TODO: Incomplete implementation of @Foo
        else if (expr instanceof ClassExpr) {
            tmpVar++;
            String tmpName = Integer.toString(tmpVar);
            IRVariable newVar = new IRVariable(tmpName);
        }

        return "%" + Integer.toString(tmpVar);
    }

    public void addToBB(ArrayList<ASTStatement> statements, ArrayList<BasicBlock> bs, String cur) {
        BasicBlock currentBlock = findBlockByName(bs, cur);

        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    int x = 1 + 1;
                } else {
                    IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                    String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                    IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                    currentBlock.addIRStatement(newIR);
                }
            } else if (statement instanceof FieldUpdate) {
                IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                currentBlock.addIRStatement(newIR);
            }
        }
    }

    public void transformToIR(ArrayList<ASTStatement> statements, BasicBlock currentBlock) {
        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    classInt++;
                    String classVar = "%" + alphabet[classInt] + "" + classNum;
                    IRVariable classVariable = new IRVariable(classVar);
                    IRAssignment alloc = new IRAssignment(classVariable, "alloc(3)");
                    IRStore store = new IRStore(classVariable, "@vtble" + alphabet[classInt]);
                    tmpVar++;
                    String tmpName2 = "%" + tmpVar;
                    IRVariable fieldIR = new IRVariable(tmpName2);
                    IRAssignment filedAlloc = new IRAssignment(fieldIR, classVar + " + 8");
                    IRStore storeField = new IRStore(fieldIR, "@fields" + alphabet[classInt]);

                    currentBlock.addIRStatement(alloc);
                    currentBlock.addIRStatement(store);
                    currentBlock.addIRStatement(filedAlloc);
                    currentBlock.addIRStatement(storeField);
                } else {
                    IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                    String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                    IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                    currentBlock.addIRStatement(newIR);
                }
            } else if (statement instanceof FieldUpdate) {
                String tmpName = "%" + tmpVar;
                String classVar = "%" + alphabet[classInt] + "" + classNum;
                IRVariable newVar = new IRVariable(tmpName);
                IRAssignment newIR = new IRAssignment(newVar, classVar + " & 1");
                Conditional newCondition = new Conditional("badptr", "l" + labelInt, newVar);
                currentBlock.addIRStatement(newIR);
                currentBlock.addIRStatement(newCondition);
            }
        }
    }


    private BasicBlock findBlockByName(ArrayList<BasicBlock> bs, String cur) {
        for (BasicBlock b : bs) {
            if (b.getName().equals(cur)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Block not found: " + cur);
    }

    public ArrayList<IRStatement> initClass(ClassNode newClass) {
        ArrayList<IRStatement> IRStatements = new ArrayList<>();
        IRStatement initClass0 = new IRSLine(newClass.getClassName() + "(this):");
        IRVariable var = new IRVariable("%" + Integer.toString(tmpVar));
        IRAssignment checkPtrBoolean = new IRAssignment(var, "%this & 1");
        Conditional newCondition = new Conditional("badptr", "l" + labelInt, var);
        labelInt++;
//        tmpVar++;
        IRStatements.add(initClass0);
        IRStatements.add(checkPtrBoolean);
        IRStatements.add(newCondition);
        return IRStatements;
    }

    public void iterateMethods(ClassNode newClass, BasicBlock classBlock) {
        ArrayList<ClassMethod> methodLists = newClass.getMethods();

        for (ClassMethod method : methodLists) {
            ArrayList<ASTStatement> statements = method.getStatements();
            transformToIR(statements, classBlock);
        }
    }
}
