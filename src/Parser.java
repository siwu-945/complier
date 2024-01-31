import AST.ASTExpression;
import AST.ASTStatement;
import Class.ClassMethod;
import Class.ClassNode;
import Class.Field;
import Expressions.Number;
import Expressions.Object;
import Expressions.*;
import Statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public Pair<ASTExpression, String> parseExpr(String input) {
        if (Character.isDigit(input.charAt(0))) {
            return parseNumber(input);
        } else if (Character.isLetter(input.charAt(0))) {
            return parseVariable(input);
        } else if (input.startsWith("(")) {
            // Parse arithmetic expression
            Pair<ASTExpression, String> leftres = parseExpr(input.substring(1));

            ASTExpression left = leftres.getFirst();
            String rest1 = leftres.getSecond();

            // extract the op part and store the remainder in rest2
            Character op = rest1.charAt(0);
            String rest2 = rest1.substring(2);
            Pair<ASTExpression, String> rightres = parseExpr(rest2);
            ASTExpression right = (Number) rightres.getFirst();
            String rest3 = rightres.getSecond();

            if (rest3.startsWith(")") && rest3.length() == 1) {
                rest3 = "";
            } else {
                rest3 = rest3.substring(2);
            }
            return new Pair<>(new ArithmeticExpression(left, op, right), rest3);

        } else if (input.startsWith("^")) {
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
                } else if (rest1.startsWith(",")) {
                    rest1 = rest1.substring(2);
                }
                Pair<ASTExpression, String> args = parseExpr(rest1);
                arguments.add(args.getFirst());
                rest1 = args.getSecond();
            }

            return new Pair<>(new Method(objectExpr, methodName, arguments), "");
        } else if (input.startsWith("&")) {
            // Parse field read
            int dotIndex = input.indexOf(".");
            Object objectExpr = new Object(input.substring(1, dotIndex));
            String name = input.substring(dotIndex + 1);

            return new Pair<>(new FieldRead(objectExpr, name), "");
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
        String[] lines = line.split("\n");
        ArrayList<String> statements = new ArrayList<>();
        for (String state : lines) {
            statements.add(state);
        }
        line = line.trim();

        if (line.startsWith("if ")) {
            int ifEnd = line.indexOf(':');
            ASTExpression ifExp;
            if (line.charAt(3) == '(') {
                String subExp = line.substring(3, ifEnd);
                ifExp = parseExpr(subExp).getFirst();
            } else {
                ifExp = parseExpr(line.substring(3, ifEnd - 1)).getFirst();
            }
            statements.remove(0);

            List<ASTStatement> trueBranch = new ArrayList<>();
            while (statements.size() != 0) {
                trueBranch.add(parseStatement(statements.get(0)));
            }
            return new IfStatement(ifExp, trueBranch, trueBranch);
        } else if (line.startsWith("while ")) {
            int whileEnd = line.indexOf(':');
            ASTExpression whileExp;
            if (line.charAt(3) == '(') {
                String subExp = line.substring(3, whileEnd);
                whileExp = parseExpr(subExp).getFirst();
            } else {
                whileExp = parseExpr(line.substring(3, whileEnd - 1)).getFirst();
            }
            statements.remove(0);

            List<ASTStatement> whileBranch = new ArrayList<>();
            while (statements.size() != 0) {
                whileBranch.add(parseStatement(statements.get(0)));
            }
            return new WhileStatement(whileExp, whileBranch);

        } else if (line.startsWith("ifonly ")) {
            int ifEnd = line.indexOf(':');
            ASTExpression ifExp;
            if (line.charAt(7) == '(') {
                String subExp = line.substring(7, ifEnd);
                ifExp = parseExpr(subExp).getFirst();
            } else {
                ifExp = parseExpr(line.substring(7, ifEnd - 1)).getFirst();
            }
            statements.remove(0);

            List<ASTStatement> trueBranch = new ArrayList<>();
            while (statements.size() != 0) {
                trueBranch.add(parseStatement(statements.get(0)));
            }
            return new IfStatement(ifExp, trueBranch, trueBranch);
        } else if (line.startsWith("return ")) {
            int returnEnd = line.indexOf(')');
            ASTExpression returnExp;
            if (line.charAt(7) == '(') {
                String subExp = line.substring(3, returnEnd);
                returnExp = parseExpr(subExp).getFirst();
            } else {
                returnExp = parseExpr(line.substring(7, returnEnd - 1)).getFirst();
            }

            return new ReturnStatement(returnExp);

        } else if (line.startsWith("}")) {
            return new endStatement();
        } else if (line.startsWith("print ") || line.startsWith("print")) {
            ASTExpression printExp;
            int printEnd = line.indexOf(')');

            if (line.charAt(5) == '(') {
                String exp = line.substring(6, printEnd);
                printExp = parseExpr(exp).getFirst();
            } else {
                printExp = parseExpr(line.substring(5)).getFirst();
            }
            return new PrintStatement(printExp);
        } else {
            int assignIndex = line.indexOf('=');
            String variableName = line.substring(0, assignIndex - 1).trim();
            String exp = line.substring(assignIndex + 2);
            ASTExpression x = parseExpr(variableName).getFirst();
            ASTExpression e = parseExpr(exp).getFirst();

            return new Assignment(x, e);
        }

    }

    public ArrayList<ASTStatement> parseStatementBlock(String codeBlock) {
        String[] lines = codeBlock.split("\n");
        ArrayList<ASTStatement> statementsBlock = new ArrayList<>();
        int currentLine = 0;
        while (currentLine < lines.length) {
            String line = lines[currentLine];
            currentLine++;

            ASTStatement newState = parseStatement(line);
            statementsBlock.add(newState);
        }
        return statementsBlock;
    }

    public ClassNode parseClass(String line) {
        String[] lines = line.split("\n");
        ArrayList<ClassMethod> methodList = new ArrayList<>();
        ArrayList<Field> fieldList = new ArrayList<>();
        ArrayList<Variable> localVar = new ArrayList<>();
        ArrayList<ASTStatement> statementList = new ArrayList<>();


        String name = "";
        //parse name
        if (lines[0].startsWith("class ")) {
            int classNameIndex = lines[0].indexOf('[');
            name = lines[0].substring(6, classNameIndex - 2);
        }
        //parse fields
        if (lines[1].startsWith("fields ") && lines[1].length() > 7) {
            String fieldsName = lines[1].substring(7);
            for (String fieldName : fieldsName.split(",")) {
                fieldList.add(new Field(fieldName));
            }
        }

        //parse methods
        int currentLine = 2;
        while (lines.length > currentLine) {
            ClassMethod methodInfo;
            ASTExpression methodExp = null;
            if (lines[currentLine].startsWith("method ")) {
                int methodEnd = lines[currentLine].indexOf(')');
                String newMethod = "^" + "this." + lines[currentLine].substring(7, methodEnd);
                //TODO: what to do with methodExp?
                methodExp = parseExpr(newMethod).getFirst();
                int localIndex = lines[currentLine].indexOf("locals");
                String localVariables = lines[currentLine].substring(localIndex + 1);
                for (String variableName : localVariables.split(",")) {
                    localVar.add(new Variable(variableName));
                }
                currentLine++;
            }
            statementList.add(parseStatement(lines[currentLine].trim()));
            methodInfo = new ClassMethod(methodExp, localVar, statementList);
            methodList.add(methodInfo);
        }

        return new ClassNode(name, fieldList, methodList);
    }

}
