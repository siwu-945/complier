package Utility;

import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import Expressions.Variable;
import Primitives.IRStatement;
import Primitives.TransformIR;
import Types.CheckStatementTypes;
import Types.ClassType;
import Types.Type;
import Types.TypeEnvironment;

import java.util.*;

import static Utility.Parser.parseClass;


public class IterateSource {
    HashMap<String, ClassNode> allClassInfo = new HashMap<String, ClassNode>();

    public int[] findClassStart(ArrayList<String> lines, int start) {
        int[] indexs = new int[2];
        indexs[0] = -999;

        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("class")) {
                indexs[0] = i;
            }
            else if (line.startsWith("]")) {
                indexs[1] = i;
                break;
            }
        }
        return indexs;
    }

    public Map<String, BasicBlock> readingSource(String codeBlock) {
        Map<String, BasicBlock> blocks = new LinkedHashMap<String, BasicBlock>();
        TransformIR irTransformer = new TransformIR();
        boolean syntaxError = false;

        Map<String, ArrayList<String>> totalFields = generateFields(codeBlock);
        Map<String, ArrayList<String>> totalMethods = generateMethods(codeBlock);
        ArrayList<String> vtbleNames = getVtableName(totalMethods);
        ArrayList<String> globalFieldArray = GlobalarrayGenerator.generateGlobalFieldArray(vtbleNames, totalFields);
        boolean inLoop = true;
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<ASTStatement> statements = new ArrayList<>();
        ArrayList<IRStatement> myIRStatements = new ArrayList<>();
        BasicBlock statementBlock = new BasicBlock(myIRStatements, "main", "non-class");
        blocks.put("main", statementBlock);
        boolean classInit = true;

        for (String line : codeBlock.split("\n")) {
            lines.add(line.trim());
        }

        TypeEnvironment mainTE = new TypeEnvironment(new HashMap<>());

        allClassInfo = GetClassInfo.getClassesInfo(lines);
        int[] classIndex = findClassStart(lines, 0);
        int currentLine = 0;
        while (inLoop) {
            //parse classes
            if (classIndex[0] != -999) {
                String classString = completeClassString(classIndex, lines);
                ClassNode newClass = parseClass(classString);
                int lineNum = classIndex[0];
                for (ClassMethod method : newClass.getMethods()) {
                    HashMap<String, Type> envMap = generateEnv(method, newClass);
                    TypeEnvironment classTE = new TypeEnvironment(envMap);
                    ArrayList<ASTStatement> methStatements = method.getStatements();
                    for (ASTStatement statemt : methStatements) {
                        boolean noError = CheckStatementTypes.checkStatementTypes(statemt, classTE, newClass);
                        if (!noError) {
                            System.out.println("Type mismatch at line: " + Integer.toString(currentLine + +4));
                            syntaxError = true;
                            break;
                        }
                        currentLine++;
                    }
                }
                ArrayList<IRStatement> IRStatements = new ArrayList<>();
                BasicBlock classBlock = new BasicBlock(IRStatements, newClass.getClassName(), "class");
                irTransformer.iterateMethods(newClass, classBlock, blocks, classInit, globalFieldArray, totalFields, totalMethods);
                blocks.put(newClass.getClassName(), classBlock);
                currentLine = classIndex[1] + 1;
                classIndex = findClassStart(lines, currentLine);
                if (classIndex[0] == -999) {
                    currentLine++;
                }
            }
            else if (lines.get(currentLine).startsWith("main ")) {
                createMainTE(lines.get(currentLine), mainTE);
                currentLine++;
            }
            else {
                ASTStatement statement = Parser.parseStatement(lines.get(currentLine));
                ClassNode nullClass = null;
                if (!CheckStatementTypes.checkStatementTypes(statement, mainTE, nullClass)) {
                    System.out.println("Type mismatch at line: " + Integer.toString(currentLine + 1));
                    syntaxError = true;
                    break;
                }
                ;
                statements.add(statement);
                currentLine++;
            }
            if (syntaxError) {
                break;
            }
            if (currentLine == lines.size()) {
                inLoop = false;
            }
        }
        if (!syntaxError) {
            classInit = false;
            irTransformer.transformToIR(statements, statementBlock, blocks, classInit);
            return blocks;
        }
        return null;
    }

    private void createMainTE(String line, TypeEnvironment mainTE) {
        int varIndex = line.indexOf("with ") + 5;
        String vars = line.substring(varIndex, line.length() - 1);
        List<String> varList = Arrays.asList(vars.split(","));
        for (String varInfo : varList) {
            int infoIndex = varInfo.indexOf(":");
            String varName = varInfo.substring(0, infoIndex);
            String varTypeString = varInfo.substring(infoIndex + 1);
            ClassNode classObj = null;
            if (!varTypeString.equals("int")) {
                classObj = allClassInfo.get(varTypeString);
                mainTE.storeTypeInfo(classObj.getClassName(), new ClassType(classObj));
            }
            Type varType = StringToType.toType(varTypeString, classObj);
            mainTE.storeTypeInfo(varName, varType);
        }
    }

    public int checkMethodType(ClassMethod method, ClassNode newClass, TypeEnvironment classTE, int lineNum) {
        ArrayList<ASTStatement> statements = method.getStatements();
        for (ASTStatement statemt : statements) {
            CheckStatementTypes.checkStatementTypes(statemt, classTE, newClass);
            lineNum++;
        }
        return lineNum;
    }

    //this + locals
    public HashMap<String, Type> generateEnv(ClassMethod method, ClassNode newClass) {
        HashMap<String, Type> envMap = new HashMap<>();
        String methodName = method.getMethodName();
        String returnType = method.getReturnType();
        ArrayList<Variable> localInfo = method.getLocalVar();
        for (Variable var : localInfo) {
            int index = var.toString().indexOf(":");
            if (index > -1) {
                String varName = var.toString().substring(0, index);
                String typeInfo = var.toString().substring(index + 1, var.toString().length());
                String lastChar = typeInfo.substring(typeInfo.length() - 1);
                if (lastChar.equals(":")) {
                    typeInfo = typeInfo.substring(0, typeInfo.length() - 1);
                }
                if (!typeInfo.equals("int")) {
                    ClassNode exprClass = allClassInfo.get(typeInfo);
                    Type varType = StringToType.toType(typeInfo, exprClass);
                    envMap.put(varName, varType);
                }
                else {
                    Type varType = StringToType.toType(typeInfo, newClass);
                    envMap.put(varName, varType);
                }
            }
        }
        Type stringToType = StringToType.toType(returnType, newClass);
        envMap.put(methodName, stringToType);
        Type classType = StringToType.toType(newClass.getClassName(), newClass);
        envMap.put("this", classType);
        return envMap;
    }

    public String completeClassString(int[] classIndex, ArrayList<String> lines) {
        int start = classIndex[0];
        int end = classIndex[1];
        String classString = "";
        for (int i = start; i < end + 1; i++) {
            classString += lines.get(i);
            if (i < end) {
                classString += "\n";
            }
        }
        return classString;
    }

    public Map<String, ArrayList<String>> generateFields(String codeBlock) {
        ArrayList<String> lines = new ArrayList<>();
        HashMap<String, ArrayList<String>> fieldMap = new LinkedHashMap<>();

        for (String line : codeBlock.split("\n")) {
            lines.add(line.trim());
        }

        int[] classIndex = findClassStart(lines, 0);
        int currentLine = 0;
        boolean inLoop = true;
        while (inLoop) {
            if (classIndex[0] != -999) {
                ArrayList<String> fieldArray = new ArrayList<>();
                String classString = completeClassString(classIndex, lines);
                ClassNode newClass = parseClass(classString);

                //pointer to vtble
                fieldArray.add("vtbl" + newClass.getClassName());

                //pointer to field array
//                fieldArray.add("fmap" + newClass.getClassName());

                ArrayList<String> fields = newClass.getFields();
                for (String field : fields) {
                    String varName = SeperateVarInfo.seperateName(field);
                    fieldArray.add(varName);
                }

                fieldMap.put(newClass.getClassName(), fieldArray);
                currentLine = classIndex[1] + 1;
                classIndex = findClassStart(lines, currentLine);
                if (classIndex[0] == -999) {
                    inLoop = false;
                }
            }
            else {
                return fieldMap;
            }
        }
        return fieldMap;
    }

    public Map<String, ArrayList<String>> generateMethods(String codeBlock) {
        ArrayList<String> lines = new ArrayList<>();
        HashMap<String, ArrayList<String>> methodMap = new LinkedHashMap<>();

        for (String line : codeBlock.split("\n")) {
            lines.add(line.trim());
        }

        int[] classIndex = findClassStart(lines, 0);
        int currentLine = 0;
        boolean inLoop = true;
        while (inLoop) {
            if (classIndex[0] != -999) {
                String classString = completeClassString(classIndex, lines);
                ClassNode newClass = parseClass(classString);

                ArrayList<String> methods = newClass.getMethodsNames();

                methodMap.put(newClass.getClassName(), methods);
                currentLine = classIndex[1] + 1;
                classIndex = findClassStart(lines, currentLine);
                if (classIndex[0] == -999) {
                    inLoop = false;
                }
            }
            else {
                return methodMap;
            }
        }
        return methodMap;
    }

    public ArrayList<String> getVtableName(Map<String, ArrayList<String>> totalMethods) {
        ArrayList<String> vtbleNames = new ArrayList<>();
        totalMethods.forEach((key, value) -> {
            vtbleNames.add(key);
        });
        return vtbleNames;
    }
}
