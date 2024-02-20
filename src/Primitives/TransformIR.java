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
    ArrayList<Integer> classes = new ArrayList<>();
    Map<String, BasicBlock> blockMap;
    BasicBlock blockCounter;

    public void tagCheck(BasicBlock currentBlock) {
        String tmpName = "%" + tmpVar;
        String classVar = "%" + alphabet[classInt] + "" + classNum;
        IRVariable newVar = new IRVariable(tmpName);
        IRAssignment newIR = new IRAssignment(newVar, classVar + " & 1");
        Conditional newCondition = new Conditional("badptr", "l" + labelInt, newVar);
        currentBlock.addIRStatement(newIR);
        currentBlock.addIRStatement(newCondition);
        tmpVar++;
    }

    public IRVariable fieldRead(Map<String, BasicBlock> blocks) {
        ArrayList<IRStatement> IRStatements = new ArrayList<>();
        String tmpName = "%" + tmpVar;
        String classVar = "%" + alphabet[classInt] + "" + classNum;

        IRVariable fieldIR = new IRVariable(tmpName);
        IRAssignment filedAlloc = new IRAssignment(fieldIR, classVar + " + 8");
        tmpVar++;
        IRVariable fieldLoadedVar = new IRVariable("%" + tmpVar);
        IRLoad loadField = new IRLoad(fieldLoadedVar, fieldIR);

        tmpVar++;
        IRVariable geteltVar = new IRVariable("%" + tmpVar);
        IRgetelt getelt = new IRgetelt(geteltVar, fieldLoadedVar, methodID);
//        tmpVar++;
        Conditional newCondition2 = new Conditional("l" + Integer.toString(labelInt + 1), "badfield", geteltVar);

        IRStatements.add(filedAlloc);
        IRStatements.add(loadField);
        IRStatements.add(getelt);
        IRStatements.add(newCondition2);
        BasicBlock newBlock = new BasicBlock(IRStatements, "l" + labelInt, "non-class");
        blocks.put("l" + labelInt, newBlock);
        labelInt++;
        blockCounter = newBlock;
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

            newBlock.addIRStatement(newLoad);
            newBlock.addIRStatement(newGet);
            newBlock.addIRStatement(newCondition2);
            blockMap.put("l" + labelInt, newBlock);
            labelInt++;
        }

        return "%" + Integer.toString(tmpVar);
    }

    public void addToBB(ArrayList<ASTStatement> statements, ArrayList<BasicBlock> bs, String cur) {
        BasicBlock currentBlock = findBlockByName(bs, cur);

        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    int x = 1 + 1;
                }
                else {
                    IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                    String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                    IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                    currentBlock.addIRStatement(newIR);
                }
            }
            else if (statement instanceof FieldUpdate) {
                IRVariable variableNode = new IRVariable(statement.getVariable().toString());
                String tmpVar = exprToIR(statement.getExpr(), currentBlock);
                IRAssignment newIR = new IRAssignment(variableNode, tmpVar);
                currentBlock.addIRStatement(newIR);
            }
        }
    }

    public void transformToIR(ArrayList<ASTStatement> statements, BasicBlock currentBlock, Map<String, BasicBlock> blocks) {
        blockCounter = currentBlock;
        for (ASTStatement statement : statements) {
            if (statement instanceof Assignment) {
                if (statement.getExpr() instanceof ClassExpr) {
                    classInt++;
                    String classVar = "%" + alphabet[classInt] + "" + classNum;
                    classes.add((int) alphabet[classInt]);
                    IRVariable classVariable = new IRVariable(classVar);
                    IRAssignment alloc = new IRAssignment(classVariable, "alloc(3)");
                    IRStore store = new IRStore(classVariable, "@vtble" + alphabet[classInt]);
                    tmpVar++;
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
                tagCheck(blockCounter);
                IRVariable geteltVar = fieldRead(blocks);
                String classVar = "%" + alphabet[classInt] + "" + classNum;
                ArrayList<IRStatement> storingRight = new ArrayList<>();
                BasicBlock storeX = new BasicBlock(storingRight, "l" + labelInt, "non-class");
                ASTExpression fieldUpdateRight = statement.getExpr();

                String tmpVarString = exprToIR(fieldUpdateRight, storeX);
                IRVariable rightVar = new IRVariable(tmpVarString);
                IRSet setRight = new IRSet(new IRVariable(classVar), geteltVar, rightVar);
                storingRight.add(setRight);
                blocks.put("l" + labelInt, storeX);
                blockCounter = storeX;
                labelInt++;
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
                    tagCheck(blockCounter);
                    fieldRead(blocks);
                    getx(blockCounter);
                }
                else {
                    ASTExpression returnValue = statement.getVariable();
                    String tmpVarString = exprToIR(returnValue, blockCounter);
                    IRVariable returnVariable = new IRVariable(tmpVarString);
                    returnControl returnStatement = new returnControl(returnVariable);
                    blockCounter.addIRStatement(returnStatement);
                }
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

    public void iterateMethods(ClassNode newClass, BasicBlock classBlock, Map<String, BasicBlock> blocks) {
        ArrayList<ClassMethod> methodLists = newClass.getMethods();
        blockMap = blocks;
        for (ClassMethod method : methodLists) {
            ArrayList<ASTStatement> statements = method.getStatements();
            transformToIR(statements, classBlock, blocks);
        }
    }
}
