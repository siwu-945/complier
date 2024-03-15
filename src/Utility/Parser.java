package Utility;

import AST.ASTExpression;
import AST.ASTStatement;
import Class.ClassMethod;
import Class.ClassNode;
import Expressions.Number;
import Expressions.Object;
import Expressions.*;
import Statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Pair<ASTExpression, String> parseExpr(String input) {
        input = input.trim();
        if (Character.isDigit(input.charAt(0))) {
            return parseNumber(input);
        }
        else if (Character.isLetter(input.charAt(0))) {
            return parseVariable(input);
        }
        else if (input.contains("==")) {
            int equalIndex = input.indexOf("==");
            int startIndex = 0;
            if (input.startsWith("(")) {
                startIndex = 1;
            }
            String leftString = input.substring(startIndex, equalIndex - 1);
            String rightString = input.substring(equalIndex + 2);
            Pair<ASTExpression, String> leftExp = parseExpr(leftString);
            Pair<ASTExpression, String> rightExp = parseExpr(rightString);

            return new Pair<>(new equality(leftExp.getFirst(), rightExp.getFirst()), rightExp.getSecond());
        }
        else if (input.startsWith("(")) {
            // Parse arithmetic expression
            Pair<ASTExpression, String> leftres = parseExpr(input.substring(1));

            ASTExpression left = leftres.getFirst();
            String rest1 = leftres.getSecond();

            // extract the op part and store the remainder in rest2
            String op = rest1.substring(0, 1);
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

    public static Pair<ASTExpression, String> parseVariable(String input) {
        int endIndex = 0;
        while (endIndex < input.length() && Character.isLetter(input.charAt(endIndex))) {
            endIndex++;
        }

        String variable = input.substring(0, endIndex);
        String remaining = input.substring(endIndex).trim();
        return new Pair<>(new Variable(variable), remaining);
    }

    public static Pair<ASTExpression, String> parseNumber(String input) {
        Matcher m = Pattern.compile("\\d+").matcher(input);
        if (m.find()) {
            String number = m.group();
            int value = Integer.parseInt(number);
            String remaining = input.substring(number.length()).trim();
            return new Pair<>(new Number(value), remaining);
        }
        throw new IllegalArgumentException("Invalid number: " + input);
    }

    public static ASTStatement parseStatement(String line) {
        line = line.trim();
        if (line.startsWith("while ")) {
            int whileEnd = line.indexOf(':');
            ASTExpression whileExp;
            if (line.charAt(3) == '(') {
                String subExp = line.substring(3, whileEnd);
                whileExp = parseExpr(subExp).getFirst();
            }
            else {
                whileExp = parseExpr(line.substring(3, whileEnd - 1)).getFirst();
            }

            ArrayList<ASTStatement> whileBranch = new ArrayList<>();
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

            ArrayList<ASTStatement> trueBranch = new ArrayList<>();

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
        else if (line.startsWith("_")) {
            int equalIndex = line.indexOf('=');
            String right_e = line.substring(equalIndex + 2);
            ASTExpression exp = parseExpr(right_e).getFirst();
            ASTExpression objectExpr = null;
            String methodName = "";
            List<ASTExpression> arguments = new ArrayList<>();
            if (exp instanceof Method) {
                objectExpr = ((Method) exp).getObject();
                methodName = ((Method) exp).getMethodName();
                arguments = ((Method) exp).getArguments();
            }
            return new MethodStatement(objectExpr, methodName, arguments);
        }
        else if (line.startsWith("^") || !line.contains("!") && line.contains("(") && line.contains(")") && line.contains(".")) {
            if (!line.startsWith("^")) {
                line = "^" + line;
            }
            int dotIndex = line.indexOf(".");
            int argStart = line.indexOf("(");
            List<ASTExpression> arguments = new ArrayList<>();

            ASTExpression objectExpr = new Object(line.substring(1, dotIndex));
            String methodName = line.substring(dotIndex + 1, argStart);
            String argString = line.substring(argStart + 1, line.length() - 1);
            if (argString.length() > 0) {
                String[] argArray = argString.split(",");
                for (String arg : argArray) {
                    Pair<ASTExpression, String> argPair = parseExpr(arg);
                    arguments.add(argPair.getFirst());
                }
            }

            return new MethodStatement(objectExpr, methodName, arguments);
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

    public static ClassNode parseClass(String line) {
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
                int methodEnd = lines[currentLine].trim().indexOf('(');
                methodName = currentLineString.substring(7, methodEnd);
                int localIndex = currentLineString.indexOf("locals");
                String localVariables = currentLineString.substring(localIndex + 7);
                for (String variableName : localVariables.split(",")) {
                    localVar.add(new Variable(variableName));
                }
                currentLine++;
            }
            String statementLine = lines[currentLine].trim();
            if (statementLine.startsWith("if ")) {
                int ifStatementEnd = parseIfStatement(lines, currentLine, statementList);
                currentLine = ifStatementEnd;
            }
            else if (statementLine.startsWith("while ")) {
                int whileStatementEnd = parseWhileStatement(lines, currentLine, statementList);
                currentLine = whileStatementEnd;
            }
            else {
                statementList.add(parseStatement(statementLine));
            }
            if (!methodName.equals("")) {
                methodInfo = new ClassMethod(methodName, localVar, statementList);
                methodList.add(methodInfo);
            }
            currentLine++;
        }

        return new ClassNode(name, fieldList, methodList);
    }

    private static int parseWhileStatement(String[] lines, int currentLine, ArrayList<ASTStatement> statementList) {
        ArrayList<ASTStatement> whileBranch = new ArrayList<>();
        ASTExpression whileExp = null;
        String line = lines[currentLine].trim();
        int whileEnd = line.indexOf(':');

        if (line.charAt(6) == '(') {
            String subExp = line.substring(6, whileEnd);
            whileExp = parseExpr(subExp).getFirst();
        }
        for (int i = currentLine + 1; i < lines.length; i++) {
            String currentLineString = lines[i].trim();
            if (currentLineString.startsWith("}")) {
                whileEnd = i;
                break;
            }
            whileBranch.add(parseStatement(currentLineString));
        }
        statementList.add(new WhileStatement(whileExp, whileBranch));
        return whileEnd;
    }

    public static int parseIfStatement(String[] lines, int currentLine, ArrayList<ASTStatement> statementList) {
        int ifEnd = 0;
        ArrayList<ASTStatement> trueBranch = new ArrayList<>();
        ArrayList<ASTStatement> falseBranch = new ArrayList<>();
        for (int i = currentLine + 1; i < lines.length; i++) {
            String currentLineString = lines[i].trim();
            if (currentLineString.startsWith("}")) {
                ifEnd = i;
                break;
            }
            trueBranch.add(parseStatement(currentLineString));
        }

        if (lines[ifEnd].contains("else")) {
            for (int i = ifEnd + 1; i < lines.length; i++) {
                String currentLineString = lines[i].trim();
                if (currentLineString.startsWith("}")) {
                    ifEnd = i;
                    break;
                }
                falseBranch.add(parseStatement(currentLineString));
            }
        }
        int ifExpEnd = lines[currentLine].indexOf(':');
        String line = lines[currentLine].trim();
        ASTExpression ifExp;
        if (line.charAt(3) == '(') {
            String subExp = line.substring(3, ifExpEnd);
            ifExp = parseExpr(subExp).getFirst();
        }
        else {
            ifExp = parseExpr(line.substring(3, ifExpEnd - 1)).getFirst();
        }
        statementList.add(new IfStatement(ifExp, trueBranch, falseBranch));
        return ifEnd;
    }

}
