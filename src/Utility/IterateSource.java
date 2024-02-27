package Utility;

import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import Expressions.Variable;
import Primitives.IRStatement;
import Primitives.TransformIR;
import Types.Type;
import Types.TypeEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static Utility.Parser.parseClass;


public class IterateSource {
    TypeEnvironment typeEnv = new TypeEnvironment(new HashMap<String, Type>());

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

        int[] classIndex = findClassStart(lines, 0);
        int currentLine = 0;
        while (inLoop) {
            //parse classes
            if (classIndex[0] != -999) {
                String classString = completeClassString(classIndex, lines);
                ClassNode newClass = parseClass(classString);
                for (ClassMethod method : newClass.getMethods()) {
                    HashMap<String, Type> envMap = generateEnv(method);
                    TypeEnvironment classTE = new TypeEnvironment(envMap);
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
                currentLine++;
            }
            else {
                ASTStatement statement = Parser.parseStatement(lines.get(currentLine));
                statements.add(statement);
                currentLine++;
            }
            if (currentLine == lines.size()) {
                inLoop = false;
            }
        }
        classInit = false;
        irTransformer.transformToIR(statements, statementBlock, blocks, classInit);
        return blocks;
    }

    private HashMap<String, Type> generateEnv(ClassMethod method) {
        HashMap<String, Type> envMap = new HashMap<>();
        String methodName = method.getMethodName();
        String returnType = method.getReturnType();
        Type stringToType = StringToType.toType(returnType);
        envMap.put(methodName, stringToType);
        return envMap;
    }

    public void addClassVariablesToEnv(ClassNode newClass) {
        ArrayList<ClassMethod> methods = newClass.getMethods();

        for (ClassMethod method : methods) {
            HashMap methodEnv = new HashMap<String, Type>();
            String methodName = method.getMethodName();
            String returnType = method.getReturnType();
            Type stringToType = StringToType.toType(returnType);
            methodEnv.put(methodName, stringToType);

            ArrayList<Variable> localVar = method.getLocalVar();
            for (Variable var : localVar) {
                String varInfo = var.toString();
                int sep = varInfo.indexOf(":");
                if (sep != -1) {
                    String varName = varInfo.substring(0, sep);
                    Type stringType = StringToType.toType(varInfo.substring(sep + 1));
                    methodEnv.put(varName, stringType);
                }
            }
//            typeEnv.addMethodEnv(methodName, methodEnv);
        }
        int x = 1;
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
                fieldArray.add("fmap" + newClass.getClassName());

                ArrayList<String> fields = newClass.getFields();
                for (String field : fields) {
                    fieldArray.add(field);
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
