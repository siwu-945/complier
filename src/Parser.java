import AST.ASTExpression;
import AST.ASTStatement;
import Expressions.Number;
import Expressions.Object;
import Expressions.*;

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
        if (line.startsWith("if ")) {

        } else if (line.startsWith("while ")) {

        } else if (line.startsWith("ifonly ")) {

        } else if (line.startsWith("return ")) {

        } else if (line.startsWith("print ")) {

        }
        return new ASTStatement();
    }
}
