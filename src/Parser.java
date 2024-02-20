import AST.ASTExpression;
import AST.ASTStatement;
import BasicBlock.BasicBlock;
import Class.ClassMethod;
import Class.ClassNode;
import Expressions.Number;
import Expressions.Object;
import Expressions.*;
import Primitives.IRStatement;
import Primitives.TransformIR;
import Statement.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public Pair<ASTExpression, String> parseExpr(String input) {
        if (Character.isDigit(input.charAt(0))) {
            return parseNumber(input);
        }
        else if (Character.isLetter(input.charAt(0))) {
            return parseVariable(input);
        }
        else if (input.startsWith("(")) {
            // Parse arithmetic expression
            Pair<ASTExpression, String> leftres = parseExpr(input.substring(1));

            ASTExpression left = leftres.getFirst();
            String rest1 = leftres.getSecond();

            // extract the op part and store the remainder in rest2
            Character op = rest1.charAt(0);
            String rest2 = rest1.substring(2);
            Pair<ASTExpression, String> rightres = parseExpr(rest2);
            ASTExpression right = rightres.getFirst();
            String rest3 = rightres.getSecond();

            if (rest3.startsWith(")") && rest3.length() == 1) {
                rest3 = "";
            }
            else if (rest3.startsWith(")") && rest3.length() >= 1) {
                rest3 = rest3.substring(1).trim();
            }
            else {
                rest3 = rest3.substring(2);
            }
            return new Pair<>(new ArithmeticExpression(left, op, right), rest3);

        }
        else if (input.startsWith("^")) {
            // Parse method invocation
            int dotIndex = input.indexOf(".");
            int argStart = input.indexOf("(");
            List<ASTExpression> arguments = new ArrayList<>();

            ASTExpression objectExpr = new Object(input.substring(1, dotIndex));
            String methodName = input.substring(dotIndex + 1, argStart);

            String rest1 = input.substring(argStart + 1);
            //recursively call on arguments.
            while (true) {
                if (rest1.startsWith(")")) {
                    break;
                }
                else if (rest1.startsWith(",")) {
                    rest1 = rest1.substring(2);
                }
                Pair<ASTExpression, String> args = parseExpr(rest1);
                arguments.add(args.getFirst());
                rest1 = args.getSecond();
            }

            return new Pair<>(new Method(objectExpr, methodName, arguments), "");
        }
        else if (input.startsWith("&")) {
            // Parse field read
            int dotIndex = input.indexOf(".");
            Object objectExpr = new Object(input.substring(1, dotIndex));
            String name = input.substring(dotIndex + 1);

            return new Pair<>(new FieldRead(objectExpr, name), "");
        }
        else if (input.startsWith("@")) {
            String className = input.substring(1);
            return new Pair<>(new ClassExpr(className), "");
        }
        // Handle other cases or throw an error
        throw new IllegalArgumentException("Invalid expression: " + input);
    }

    public Pair<ASTExpression, String> parseVariable(String input) {
        int endIndex = 0;
        while (endIndex < input.length() && Character.isLetter(input.charAt(endIndex))) {
            endIndex++;
        }

        String variable = input.substring(0, endIndex);
        String remaining = input.substring(endIndex).trim();
        return new Pair<>(new Variable(variable), remaining);
    }

    public Pair<ASTExpression, String> parseNumber(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        if (m.find()) {
            String number = m.group();
            int value = Integer.parseInt(number);
            String remaining = input.substring(number.length()).trim();
            return new Pair<>(new Number(value), remaining);
        }
        throw new IllegalArgumentException("Invalid number: " + input);
    }

    public ASTStatement parseStatement(String line) {
        line = line.trim();
        if (line.startsWith("if ")) {
            int ifEnd = line.indexOf(':');
            ASTExpression ifExp;
            if (line.charAt(3) == '(') {
                String subExp = line.substring(3, ifEnd);
                ifExp = parseExpr(subExp).getFirst();
            }
            else {
                ifExp = parseExpr(line.substring(3, ifEnd - 1)).getFirst();
            }

            List<ASTStatement> trueBranch = new ArrayList<>();

            return new IfStatement(ifExp, trueBranch, trueBranch);
        }
        else if (line.startsWith("while ")) {
            int whileEnd = line.indexOf(':');
            ASTExpression whileExp;
            if (line.charAt(3) == '(') {
                String subExp = line.substring(3, whileEnd);
                whileExp = parseExpr(subExp).getFirst();
            }
            else {
                whileExp = parseExpr(line.substring(3, whileEnd - 1)).getFirst();
            }

            List<ASTStatement> whileBranch = new ArrayList<>();
            return new WhileStatement(whileExp, whileBranch);

        }
        else if (line.startsWith("ifonly ")) {
            int ifEnd = line.indexOf(':');
            ASTExpression ifExp;
            if (line.charAt(7) == '(') {
                String subExp = line.substring(7, ifEnd);
                ifExp = parseExpr(subExp).getFirst();
            }
            else {
                ifExp = parseExpr(line.substring(7, ifEnd - 1)).getFirst();
            }

            List<ASTStatement> trueBranch = new ArrayList<>();

            return new IfStatement(ifExp, trueBranch, trueBranch);
        }
        else if (line.startsWith("return ")) {
            int returnEnd = line.indexOf(')');
            ASTExpression returnExp;
            if (line.charAt(7) == '(' || line.charAt(7) == '&') {
                String subExp = "";
                int startIndex = 3;
                if (line.charAt(7) == '&') {
                    startIndex = 7;
                }
                if (returnEnd > -1) {
                    subExp = line.substring(startIndex, returnEnd);
                }
                else {
                    subExp = line.substring(startIndex);
                }
                returnExp = parseExpr(subExp).getFirst();
            }
            else {
                returnExp = parseExpr(line.substring(7)).getFirst();
            }

            return new ReturnStatement(returnExp);

        }
        else if (line.startsWith("}")) {
            return new endStatement();
        }
        else if (line.startsWith("print ") || line.startsWith("print")) {
            ASTExpression printExp;
            int printEnd = line.indexOf(')');

            if (printEnd < line.length() - 1) {
                printEnd = line.length() - 1;
            }

            if (line.charAt(5) == '(') {
                String exp = line.substring(6, printEnd);
                printExp = parseExpr(exp).getFirst();
            }
            else {
                printExp = parseExpr(line.substring(5)).getFirst();
            }
            return new PrintStatement(printExp);
        }
        else if (line.startsWith("!")) {
            int eEnd = line.indexOf('.');
            int equalIndex = line.indexOf('=');

            ASTExpression left_e = new ClassExpr(line.substring(1, eEnd));
            String field = line.substring(eEnd + 1, equalIndex - 1);
            Pair<ASTExpression, String> right_ePair = parseExpr(line.substring(equalIndex + 2));
            ASTExpression right_e = right_ePair.getFirst();
            return new FieldUpdate(left_e, field, right_e);
        }
        else {
            int assignIndex = line.indexOf('=');
            String variableName = line.substring(0, assignIndex - 1).trim();
            String exp = line.substring(assignIndex + 2);
            ASTExpression x = parseExpr(variableName).getFirst();
            Pair<ASTExpression, String> expPair = parseExpr(exp);
            ASTExpression e = expPair.getFirst();

            return new Assignment(x, e);
        }

    }

    public ClassNode parseClass(String line) {
        String[] lines = line.split("\n");
        ArrayList<ClassMethod> methodList = new ArrayList<>();
        ArrayList<String> fieldList = new ArrayList<>();

        String name = "";
        //parse name
        if (lines[0].startsWith("class ")) {
            int classNameIndex = lines[0].indexOf('[');
            name = lines[0].substring(6, classNameIndex).trim();
        }
        //parse fields
        if (lines[1].trim().startsWith("fields ") && lines[1].length() > 7) {
            String fieldsName = lines[1].trim().substring(7);
            for (String fieldName : fieldsName.split(",")) {
                fieldList.add(fieldName.trim());
            }
        }

        //parse methods
        int currentLine = 2;
        ArrayList<ASTStatement> statementList = new ArrayList<>();
        ArrayList<Variable> localVar = new ArrayList<>();

        while (!lines[currentLine].startsWith("]")) {
            ClassMethod methodInfo;
            String methodName = "";
            String currentLineString = lines[currentLine].trim();
            if (currentLineString.startsWith("method ")) {
                statementList = new ArrayList<>();
                localVar = new ArrayList<>();
                int methodEnd = lines[currentLine].trim().indexOf(')');
                methodName = currentLineString.substring(7, methodEnd - 1);
                int localIndex = currentLineString.indexOf("locals");
                String localVariables = currentLineString.substring(localIndex + 7);
                for (String variableName : localVariables.split(",")) {
                    localVar.add(new Variable(variableName));
                }
                currentLine++;
            }
            String statementLine = lines[currentLine].trim();
            statementList.add(parseStatement(statementLine));
            if (!methodName.equals("")) {
                methodInfo = new ClassMethod(methodName, localVar, statementList);
                methodList.add(methodInfo);
            }
            currentLine++;
        }

        return new ClassNode(name, fieldList, methodList);
    }

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
        boolean inLoop = true;
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<ASTStatement> statements = new ArrayList<>();
        ArrayList<IRStatement> myIRStatements = new ArrayList<>();
        BasicBlock statementBlock = new BasicBlock(myIRStatements, "main", "non-class");
        blocks.put("main", statementBlock);


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

                ArrayList<IRStatement> IRStatements = new ArrayList<>();

                BasicBlock classBlock = new BasicBlock(IRStatements, newClass.getClassName(), "class");
                irTransformer.iterateMethods(newClass, classBlock, blocks);
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
                ASTStatement statement = parseStatement(lines.get(currentLine));
                statements.add(statement);
                currentLine++;
            }
            if (currentLine == lines.size()) {
                inLoop = false;
            }
        }
        irTransformer.transformToIR(statements, statementBlock, blocks);
        return blocks;
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
        }
        return methodMap;
    }
}
