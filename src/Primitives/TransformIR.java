package Primitives;

import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import ControlTransfer.Conditional;
import ControlTransfer.Jump;
import ControlTransfer.returnControl;
import Expressions.Number;
import Expressions.*;
import Statement.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TransformIR {
    static HashMap<String, String> localClassFields = new HashMap<>();
    ArrayList<String> globalFields;
    Map<String, ArrayList<String>> totalFields;
    int tmpVar = 0;
    int labelInt = 1;
    int classInt = 0;
    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    int classNum = 0;
    ArrayList<String> classes = new ArrayList<>();
    Map<String, BasicBlock> blockMap;
    BasicBlock blockCounter;
    Map<String, ArrayList<String>> totalMethods;
    String currentClassName;

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

    public IRVariable fieldRead(Map<String, BasicBlock> blocks, boolean classInit, String currentObj, String fieldName) {
        ArrayList<IRStatement> IRStatements = new ArrayList<>();
        String classObjName = "";
        String classVar = "";
        if (classInit) {
            classVar = "%this";
            classObjName = currentClassName;
        }
        else {
            int currentClassIndex = classIndex(classes, currentObj);
            Character className = alphabet[currentClassIndex];
            classVar = "%" + className + "" + classNum;
            classObjName = currentObj.substring(1);
        }
        if (!currentObj.substring(1).equals("this") && !currentObj.contains("this")) {
            classObjName = currentObj.substring(1);
        }
        String tmpName = "%" + tmpVar;

        IRVariable fieldIR = new IRVariable(tmpName);
        IRAssignment filedAlloc = new IRAssignment(fieldIR, classVar + " + 8");
        tmpVar++;
        IRVariable fieldLoadedVar = new IRVariable("%" + tmpVar);
        IRLoad loadField = new IRLoad(fieldLoadedVar, fieldIR);

        tmpVar++;
        IRVariable geteltVar = new IRVariable("%" + tmpVar);

        String currentClass = localClassFields.get(classObjName);

        //+2 so to jump over vtbl and addresses
        int fieldId = totalFields.get(currentClass).indexOf(fieldName);
        IRgetelt getelt = new IRgetelt(geteltVar, fieldLoadedVar, fieldId);
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
        blockMap.put("l" + labelInt, blockCounter);
        return geteltVar;
    }

    public void getx(String classString, int fieldID) {

        String tmpName = "%" + tmpVar;
        IRVariable classVar = new IRVariable(classString);
        IRVariable newVar = new IRVariable(tmpName);
        IRgetelt getX = new IRgetelt(newVar, classVar, fieldID);
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
            String op = ((ArithmeticExpression) expr).getOp();
            tmpVar++;
            IRVariable newVar = new IRVariable("%" + Integer.toString(tmpVar));
            IRAssignment newIR = new IRAssignment(newVar, leftVar + " " + op + " " + rightVar);
            blockCounter.addIRStatement(newIR);
        }
        else if (expr instanceof Number) {
            tmpVar++;
            String tmpName = "%" + Integer.toString(tmpVar);
            IRVariable newVar = new IRVariable(tmpName);
            IRAssignment newAssign = new IRAssignment(newVar, expr.toString());
            blockCounter.addIRStatement(newAssign);
        }
        else if (expr instanceof Method) {
            String currentClass = ((Method) expr).currentClass();
            String className = "";
            if (currentClass.equals("this")) {
                className = "%this";
            }
            else {
                int currentClassIndex = classIndex(classes, "^" + currentClass);
                className = Character.toString(alphabet[currentClassIndex]);
            }
            String classVar = "%" + className + "" + classNum;

            tagCheck(blockCounter, classVar);
            ArrayList<IRStatement> empty = new ArrayList<>();
            IRSLine badPtrLine = new IRSLine("fail NotAPointer");
            empty.add(badPtrLine);
            BasicBlock badPtr = new BasicBlock(empty, "badptr", "non-class");
            ArrayList<IRStatement> irStatements = new ArrayList<>();
            BasicBlock newBlock = new BasicBlock(irStatements, "l" + labelInt, "non-class");
            String tmpName2 = "%" + tmpVar;
            IRVariable newVar2 = new IRVariable(tmpName2);
            IRLoad newLoad = new IRLoad(newVar2, new IRVariable(classVar));

            tmpVar++;
            IRVariable newVar3 = new IRVariable("%" + tmpVar);

            AtomicReference<String> currentMethodClass = new AtomicReference<>("");
            if (currentClass.equals("this")) {
                totalMethods.forEach((k, v) -> {
                    if (v.contains(((Method) expr).getMethodName())) {
                        currentMethodClass.set(k);
                    }
                });
                currentClass = currentMethodClass.get();
            }
            String objClassName = localClassFields.get(currentClass);
            int methodID = totalMethods.get(objClassName).indexOf(((Method) expr).getMethodName());
            IRgetelt newGet = new IRgetelt(newVar3, newVar2, methodID);
            Conditional newCondition2 = new Conditional("l" + Integer.toString(labelInt + 1), "badmethod", newVar3);

            ArrayList<IRStatement> empty2 = new ArrayList<>();
            IRSLine badMethodLine = new IRSLine("fail NoSuchMethod");
            empty2.add(badMethodLine);
            BasicBlock badMethod = new BasicBlock(empty2, "badmethod", "non-class");

            newBlock.addIRStatement(newLoad);
            newBlock.addIRStatement(newGet);
            newBlock.addIRStatement(newCondition2);
            blockCounter = newBlock;
            blockMap.put("l" + labelInt, newBlock);
            blockMap.put("badptr", badPtr);
            blockMap.put("badmethod", badMethod);
            labelInt++;
        }
        else if (expr instanceof equality) {
            String leftVar = exprToIR(((equality) expr).getLeft(), blockCounter);
            String rightVar = exprToIR(((equality) expr).getRight(), blockCounter);
            String op = "==";
            tmpVar++;
            IRVariable newVar = new IRVariable("%" + Integer.toString(tmpVar));
            IRAssignment newIR = new IRAssignment(newVar, leftVar + " " + op + " " + rightVar);
            blockCounter.addIRStatement(newIR);
        }
        else if (expr instanceof FieldRead) {
            boolean classInit = false;
            Map<String, BasicBlock> blocks = new HashMap<>();
            String objectName = "";
            String classObjcName = "";
            if (((FieldRead) expr).getObj().contains("this")) {
                objectName = "%this";
                classObjcName = currentClassName;
                classInit = true;
            }
            else {
                objectName = ((FieldRead) expr).getObj();
                classObjcName = objectName;
            }
            String fieldName = ((FieldRead) expr).getField();

            String currentClass = localClassFields.get(classObjcName);
            int fieldId = totalFields.get(currentClass).indexOf(fieldName);

            tagCheck(blockCounter, objectName);
            fieldRead(blocks, classInit, "@" + objectName, fieldName);

            getx(objectName, fieldId);
            BasicBlock newBlock = new BasicBlock(new ArrayList<>(), "l" + labelInt, "non-class");
            blockMap.put("l" + labelInt, newBlock);
            blockCounter = newBlock;
            labelInt++;
        }

        return "%" + Integer.toString(tmpVar);
    }

    public void transformToIR(ArrayList<ASTStatement> statements, BasicBlock currentBlock, Map<String, BasicBlock> blocks, boolean classInit) {
        blockCounter = currentBlock;
        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    String classVar = "%" + alphabet[classInt] + "" + classNum;
                    classes.add(statement.getVariable().toString());
                    localClassFields.put(statement.getVariable().toString(), ((ClassExpr) statement.getExpr()).getClassName());
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
                    classInt++;

                }
                else {
                    IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                    String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                    IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                    blockCounter.addIRStatement(newIR);
                }
            }
            else if (statement instanceof FieldUpdate) {
                String currentClass = statement.getVariable().toString();

                ArrayList<IRStatement> storingRight = new ArrayList<>();
                BasicBlock storeX = new BasicBlock(storingRight, "l" + labelInt, "non-class");

                String classVar = "";
                if (classInit) {
                    classVar = "%this";
                }
                else {
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
                String field = ((FieldUpdate) statement).getField();
                if (!classVar.equals("%this")) {
                    currentClassName = ((FieldUpdate) statement).getVariable().toString().substring(1);
                }
                else {
                    boolean found = false;
                }
                IRVariable geteltVar = fieldRead(blocks, classInit, currentClass, field);
                BasicBlock newBlock = new BasicBlock(new ArrayList<>(), "l" + labelInt, "non-class");
                blocks.put("l" + labelInt, newBlock);
                blockCounter = newBlock;
                labelInt++;
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
                    String obj = ((Method) statement.getExpr()).currentClass();
                    int classInt = classIndex(classes, "^" + obj);
                    IRVariable classObject = new IRVariable("%" + alphabet[classInt] + "" + classNum);
                    IRVariable returnVarible = new IRVariable("%" + tmpVar);
                    IRcall getValue = new IRcall(codeAddress, classObject, returnVarible);

                    ArrayList<IRStatement> IRStatements = new ArrayList<>();
                    BasicBlock printBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
                    blockCounter = printBlock;
                    blockCounter.addIRStatement(getValue);
                    IRPrint newIR = new IRPrint(returnVarible.toString());
                    blockCounter.addIRStatement(newIR);
                    blockMap.put("l" + labelInt, blockCounter);
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
                    String classObjcName = "";
                    if (classInit) {
                        objectName = "%this";
                        classObjcName = currentClassName;
                    }
                    else {
                        objectName = ((FieldRead) statement.getExpr()).getObj();
                        classObjcName = objectName;
                    }
                    String fieldName = ((FieldRead) statement.getExpr()).getField();

                    String currentClass = localClassFields.get(classObjcName);
                    int fieldId = totalFields.get(currentClass).indexOf(fieldName);

                    tagCheck(blockCounter, objectName);
                    fieldRead(blocks, classInit, "@" + objectName, fieldName);
                    getx(objectName, fieldId);
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
                    else if (!blockCounter.getName().contains("trueblock") && !blockCounter.getName().contains("falseblock")) {
                        boolean hasControl = false;
                        for (IRStatement ir : blockCounter.getIRStatements()) {
                            if (ir instanceof returnControl) {
                                hasControl = true;
                            }
                        }
                        if (!hasControl) {
                            blockMap.put("l" + labelInt, blockCounter);
                        }
                        BasicBlock newBlock = new BasicBlock(new ArrayList<>(), "l" + labelInt, "non-class");
                        blockCounter = newBlock;
                        labelInt++;
                    }
                }
            }
            else if (statement instanceof MethodStatement) {
                ASTExpression printVal = statement.getVariable();
                String tmpVarString = exprToIR(printVal, blockCounter);
                IRVariable codeAddress = new IRVariable(tmpVarString);
                tmpVar++;
                String obj = statement.getExpr().toString();
                if (obj.contains("= ^")) {
                    int objStart = obj.indexOf("= ^");
                    obj = obj.substring(objStart + 3);
                }
                int classInt = classIndex(classes, "^" + obj);
                String methodObjName = "";
                if (obj.contains("this")) {
                    methodObjName = "%this";
                }
                else {
                    methodObjName = "%" + alphabet[classInt] + "" + classNum;
                }
                IRVariable classObject = new IRVariable(methodObjName);
                IRVariable returnVarible = new IRVariable("%" + tmpVar);
                IRcall getValue = new IRcall(codeAddress, classObject, returnVarible);

                ArrayList<IRStatement> IRStatements = new ArrayList<>();
                BasicBlock printBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
                blockCounter = printBlock;
                blockCounter.addIRStatement(getValue);
                IRPrint newIR = new IRPrint(returnVarible.toString());
                blockCounter.addIRStatement(newIR);
                blockMap.put("l" + labelInt, blockCounter);
            }
            else if (statement instanceof IfStatement) {
                ArrayList<ASTStatement> trueBranch = ((IfStatement) statement).getTrueBranch();
                ArrayList<ASTStatement> falseBranch = ((IfStatement) statement).getFalseBranch();
                String conditionExp = exprToIR(statement.getExpr(), blockCounter);
                IRVariable condition = new IRVariable(conditionExp);
                ArrayList<IRStatement> trueIR = new ArrayList<>();
                ArrayList<IRStatement> falseIR = new ArrayList<>();

                String trueBlockName = "trueblock" + labelInt;
                String falseBlockName = "falseblock" + labelInt;
                labelInt++;
                if (falseBranch.size() == 0) {
                    falseBlockName = "null";
                }
                Conditional ifConditional = new Conditional(trueBlockName, falseBlockName, condition);
                blockCounter.addIRStatement(ifConditional);
                BasicBlock trueBlock = new BasicBlock(trueIR, trueBlockName, "non-class");
                blockCounter = trueBlock;
                blockMap.put(trueBlockName, blockCounter);
                transformToIR(trueBranch, blockCounter, blocks, classInit);
                if (falseBranch.size() != 0) {
                    BasicBlock falseBlock = new BasicBlock(falseIR, falseBlockName, "non-class");
                    blockMap.put(falseBlockName, falseBlock);
                    blockCounter = falseBlock;
                    transformToIR(falseBranch, blockCounter, blocks, classInit);
                }
            }
            else if (statement instanceof WhileStatement) {
                ArrayList<ASTStatement> whileBranch = ((WhileStatement) statement).getWhileBranch();
                String whileBlockName = "whileblock";
                Jump condiionalJump = new Jump(whileBlockName);
                blockCounter.addIRStatement(condiionalJump);

                BasicBlock whileBlock = new BasicBlock(new ArrayList<>(), "whileblock" + labelInt, "non-class");
                blockCounter = whileBlock;
                blockMap.put("whileblock" + labelInt, blockCounter);
                labelInt++;
                transformToIR(whileBranch, blockCounter, blocks, classInit);
            }
        }
        if (!classInit) {
            IRVariable defaultReturn = new IRVariable("0");
            blockCounter.addIRStatement(new returnControl(defaultReturn));
        }
    }

    public void iterateMethods(ClassNode newClass, BasicBlock classBlock, Map<String, BasicBlock> blocks, boolean classInit, ArrayList<String> gfields, Map<String, ArrayList<String>> totalFieldsMap, Map<String, ArrayList<String>> totalMethodsMap) {
        ArrayList<ClassMethod> methodLists = newClass.getMethods();
        globalFields = gfields;
        totalFields = totalFieldsMap;
        localClassFields.put(newClass.getClassName(), newClass.getClassName());
        blockMap = blocks;
        totalMethods = totalMethodsMap;
        currentClassName = newClass.getClassName();
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
