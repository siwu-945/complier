import AST.ASTExpression;
import Expressions.Number;
import Expressions.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Pair<ASTExpression, String> parseExpr(String input) {
        input = input.trim();
        if (Character.isDigit(input.charAt(0))) {
            // Parse number literal
            return parseNumber(input);
        } else if (Character.isLetter(input.charAt(0))) {
            // Parse
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

            Pair<ASTExpression, String> object = parseExpr(input.substring(0, dotIndex));

            ASTExpression objectExpr = object.getFirst();
            String rest1 = object.getSecond();
            String methodName = rest1.substring(dotIndex + 1, argStart);
            Pair<ASTExpression, String> args = parseExpr(rest1.substring(argStart + 1));

            List<ASTExpression> arguments;
            return new Pair<>(new Method(objectExpr, methodName, arguments));
        }
//        else if (input.startsWith("&")) {
//            // Parse field read
//            return parseFieldRead(input);
//        }
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
}
