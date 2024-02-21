package Primitives;

import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import ControlTransfer.Conditional;
import ControlTransfer.returnControl;
import Expressions.Number;
import Expressions.*;
import Statement.Assignment;
import Statement.FieldUpdate;
import Statement.PrintStatement;
import Statement.ReturnStatement;

import java.util.ArrayList;
import java.util.Map;

public class TransformIR {
    int tmpVar = 0;
    int labelInt = 1;
    int classInt = 0;
    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    int classNum = 0;
    int methodID = 0;
    ArrayList<String> classes = new ArrayList<>();
    Map<String, BasicBlock> blockMap;
    BasicBlock blockCounter;

    //TODO: fix the getting the correct index
    public int classIndex(ArrayList<String> classArray, String name) {
        String className = name.substring(1);
        for (int i = 0; i < classArray.size(); i++) {
            if (classArray.get(i).equals(className)) {
                return i;
            }
        }
        return -1;
    }

    public void tagCheck(BasicBlock currentBlock, String className) {
        if (className.equals("%this")) {
            return;
        }
        String tmpName = "%" + tmpVar;
        IRVariable newVar = new IRVariable(tmpName);
        IRAssignment newIR = new IRAssignment(newVar, className + " & 1");
        Conditional newCondition = new Conditional("badptr", "l" + labelInt, newVar);
        currentBlock.addIRStatement(newIR);
        currentBlock.addIRStatement(newCondition);
        tmpVar++;
    }

    public IRVariable fieldRead(Map<String, BasicBlock> blocks, boolean classInit) {
        ArrayList<IRStatement> IRStatements = new ArrayList<>();
        String classVar = "";
        if (classInit) {
            classVar = "%this";
        }
        else {
            classVar = "%" + alphabet[classInt] + "" + classNum;
        }
        String tmpName = "%" + tmpVar;

        IRVariable fieldIR = new IRVariable(tmpName);
        IRAssignment filedAlloc = new IRAssignment(fieldIR, classVar + " + 8");
        tmpVar++;
        IRVariable fieldLoadedVar = new IRVariable("%" + tmpVar);
        IRLoad loadField = new IRLoad(fieldLoadedVar, fieldIR);

        tmpVar++;
        IRVariable geteltVar = new IRVariable("%" + tmpVar);

        IRgetelt getelt = new IRgetelt(geteltVar, fieldLoadedVar, methodID);
        Conditional newCondition2 = new Conditional("l" + Integer.toString(labelInt), "badfield", geteltVar);
        ArrayList<IRStatement> empty = new ArrayList<>();
        IRSLine badFieldLine = new IRSLine("fail NoSuchField");
        empty.add(badFieldLine);
        BasicBlock badField = new BasicBlock(empty, "badfield", "non-class");
        blockMap.put("badfield", badField);
        blockCounter.addIRStatement(filedAlloc);
        blockCounter.addIRStatement(loadField);
        blockCounter.addIRStatement(getelt);
        blockCounter.addIRStatement(newCondition2);

        if (!classInit) {
            BasicBlock newBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
            blocks.put("l" + labelInt, newBlock);
            blockCounter = newBlock;
            labelInt++;
        }
        return geteltVar;
    }

    public void getx(BasicBlock currentBlock) {
        String tmpName = "%" + tmpVar;
        String classVarString = "%" + alphabet[classInt] + "" + classNum;
        IRVariable classVar = new IRVariable(classVarString);
        IRVariable newVar = new IRVariable(tmpName);
        IRgetelt getX = new IRgetelt(newVar, classVar, methodID);
        returnControl returnGet = new returnControl(newVar);
        ArrayList<IRStatement> IRStatements = new ArrayList<>();
        IRStatements.add(getX);
        IRStatements.add(returnGet);
        BasicBlock newBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
        blockMap.put("l" + labelInt, newBlock);
        blockCounter = newBlock;
        labelInt++;
    }

    public String exprToIR(ASTExpression expr, BasicBlock currentBlock) {

        if (expr instanceof ArithmeticExpression) {
            String leftVar = exprToIR(((ArithmeticExpression) expr).getLeft(), currentBlock);
            String rightVar = exprToIR(((ArithmeticExpression) expr).getRight(), currentBlock);
            Character op = ((ArithmeticExpression) expr).getOp();
            tmpVar++;
            IRVariable newVar = new IRVariable("%" + Integer.toString(tmpVar));
            IRAssignment newIR = new IRAssignment(newVar, leftVar + " " + op + " " + rightVar);
            currentBlock.addIRStatement(newIR);
        }
        else if (expr instanceof Number) {
            tmpVar++;
            String tmpName = "%" + Integer.toString(tmpVar);
            IRVariable newVar = new IRVariable(tmpName);
            IRAssignment newAssign = new IRAssignment(newVar, expr.toString());
            currentBlock.addIRStatement(newAssign);
        }
        else if (expr instanceof Method) {
            String tmpName = "%" + tmpVar;
            IRVariable newVar = new IRVariable(tmpName);
            String classVar = "%" + alphabet[classInt] + "" + classNum;
            IRAssignment newIR = new IRAssignment(newVar, classVar + " & 1");
            Conditional newCondition = new Conditional("badptr", "l" + labelInt, newVar);
            ArrayList<IRStatement> empty = new ArrayList<>();
            IRSLine badPtrLine = new IRSLine("fail NotAPointer");
            empty.add(badPtrLine);
            BasicBlock badPtr = new BasicBlock(empty, "badptr", "non-class");

            currentBlock.addIRStatement(newIR);
            currentBlock.addIRStatement(newCondition);
            tmpVar++;

            ArrayList<IRStatement> irStatements = new ArrayList<>();
            BasicBlock newBlock = new BasicBlock(irStatements, "l" + labelInt, "non-class");
            String tmpName2 = "%" + tmpVar;
            IRVariable newVar2 = new IRVariable(tmpName2);
            IRLoad newLoad = new IRLoad(newVar2, new IRVariable(classVar));

            tmpVar++;
            IRVariable newVar3 = new IRVariable("%" + tmpVar);
            IRgetelt newGet = new IRgetelt(newVar3, newVar2, methodID);
            Conditional newCondition2 = new Conditional("l" + Integer.toString(labelInt + 1), "badmethod", newVar3);

            ArrayList<IRStatement> empty2 = new ArrayList<>();
            IRSLine badMethodLine = new IRSLine("fail NoSuchMethod");
            empty2.add(badMethodLine);
            BasicBlock badMethod = new BasicBlock(empty2, "badmethod", "non-class");

            newBlock.addIRStatement(newLoad);
            newBlock.addIRStatement(newGet);
            newBlock.addIRStatement(newCondition2);
            blockMap.put("l" + labelInt, newBlock);
            blockMap.put("badptr", badPtr);
            blockMap.put("badmethod", badMethod);
            labelInt++;
        }

        return "%" + Integer.toString(tmpVar);
    }

    //TODO: take consideration of classInit
    public void transformToIR(ArrayList<ASTStatement> statements, BasicBlock currentBlock, Map<String, BasicBlock> blocks, boolean classInit) {
        classInt = 0;
        blockCounter = currentBlock;
        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    classInt++;
                    String classVar = "%" + alphabet[classInt] + "" + classNum;
                    classes.add(statement.getVariable().toString());
                    IRVariable classVariable = new IRVariable(classVar);
                    IRAssignment alloc = new IRAssignment(classVariable, "alloc(3)");
                    IRStore store = new IRStore(classVariable, "@vtble" + alphabet[classInt]);
//                    tmpVar++;
                    String tmpName2 = "%" + tmpVar;
                    IRVariable fieldIR = new IRVariable(tmpName2);
                    IRAssignment filedAlloc = new IRAssignment(fieldIR, classVar + " + 8");
                    IRStore storeField = new IRStore(fieldIR, "@fields" + alphabet[classInt]);
                    tmpVar++;

                    currentBlock.addIRStatement(alloc);
                    currentBlock.addIRStatement(store);
                    currentBlock.addIRStatement(filedAlloc);
                    currentBlock.addIRStatement(storeField);
                }
                else {
                    IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                    String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                    IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                    currentBlock.addIRStatement(newIR);
                }
            }
            else if (statement instanceof FieldUpdate) {
                ArrayList<IRStatement> storingRight = new ArrayList<>();
                BasicBlock storeX = new BasicBlock(storingRight, "l" + labelInt, "non-class");

                String classVar = "";
                if (classInit) {
                    classVar = "%this";
                }
                else {
                    String currentClass = statement.getVariable().toString();
                    int currentClassIndex = classIndex(classes, currentClass);
                    Character className = alphabet[currentClassIndex];
                    classVar = "%" + className + "" + classNum;
                }
                tagCheck(blockCounter, classVar);
                if (!classInit) {
                    blockCounter = storeX;
                    blocks.put("l" + labelInt, blockCounter);
                    labelInt++;
                }
                IRVariable geteltVar = fieldRead(blocks, classInit);
                ASTExpression fieldUpdateRight = statement.getExpr();

                String tmpVarString = exprToIR(fieldUpdateRight, blockCounter);
                IRVariable rightVar = new IRVariable(tmpVarString);
                IRSet setRight = new IRSet(new IRVariable(classVar), geteltVar, rightVar);
                blockCounter.addIRStatement(setRight);
                tmpVar++;
            }
            else if (statement instanceof PrintStatement) {
                ASTExpression printVal = statement.getVariable();
                String tmpVarString = exprToIR(printVal, blockCounter);
                IRVariable codeAddress = new IRVariable(tmpVarString);
                tmpVar++;
                if (statement.getExpr() instanceof Method) {
                    IRVariable classObject = new IRVariable("%" + alphabet[classInt] + "" + classNum);
                    IRVariable returnVarible = new IRVariable("%" + tmpVar);
                    IRcall getValue = new IRcall(codeAddress, classObject, returnVarible);

                    ArrayList<IRStatement> IRStatements = new ArrayList<>();
                    BasicBlock printBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
                    blockCounter = printBlock;
                    blockCounter.addIRStatement(getValue);
                    IRPrint newIR = new IRPrint(returnVarible.toString());
                    blockCounter.addIRStatement(newIR);
                    blocks.put("l" + labelInt, printBlock);
                }
                else {
                    IRVariable returnVarible = new IRVariable("%" + tmpVar);
                    returnControl returnStatement = new returnControl(returnVarible);
                    blockCounter.addIRStatement(returnStatement);
                }

            }
            else if (statement instanceof ReturnStatement) {
                if (statement.getExpr() instanceof FieldRead) {
                    String objectName = "";
                    if (classInit) {
                        objectName = "%this";
                    }
                    else {
                        objectName = ((FieldRead) statement.getExpr()).getObj();
                    }
                    tagCheck(blockCounter, objectName);
                    fieldRead(blocks, classInit);
                    tmpVar++;
                    getx(blockCounter);
                }
                else {
                    ASTExpression returnValue = statement.getVariable();
                    String tmpVarString = exprToIR(returnValue, blockCounter);
                    IRVariable returnVariable = new IRVariable(tmpVarString);
                    returnControl returnStatement = new returnControl(returnVariable);
                    blockCounter.addIRStatement(returnStatement);
                    if (classInit && blockCounter.getAttribute().equals("class")) {
                        blockMap.put(blockCounter.getName(), blockCounter);
                    }
                    else {
                        blockMap.put("l" + labelInt, blockCounter);
                        labelInt++;
                    }
                }
            }
        }
    }

    public void iterateMethods(ClassNode newClass, BasicBlock classBlock, Map<String, BasicBlock> blocks, boolean classInit, boolean isNewClass) {
        ArrayList<ClassMethod> methodLists = newClass.getMethods();
        blockMap = blocks;
        for (int i = 0; i < methodLists.size(); i++) {
            ClassMethod method = methodLists.get(i);
            ArrayList<ASTStatement> statements = method.getStatements();
            if (i > 0) {
                ArrayList<IRStatement> IRStatements = new ArrayList<>();
                BasicBlock newMethodBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
                classBlock = newMethodBlock;
                blockCounter = classBlock;
                blockMap.put("l" + labelInt, classBlock);
            }
            transformToIR(statements, classBlock, blocks, classInit);
        }
    }
}
