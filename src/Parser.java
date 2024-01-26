import AST.ASTExpression;
import Expressions.ArithmeticExpression;
import Expressions.Number;
import Expressions.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Pair<ASTExpression, String> parseExpr(String input) {
        input = input.trim();
        if (Character.isDigit(input.charAt(0))) {
            // Parse number literal
            return parseNumber(input);
        }
//
//        else if (Character.isLetter(input.charAt(0))) {
//            // Parse variable
//            return parseVariable(input);
        else if (input.startsWith("(")) {
            // Parse arithmetic expression

            Pair<ASTExpression, String> leftres = parseExpr(input.substring(1, input.length()));

            ASTExpression left = leftres.getFirst();
            String rest1 = leftres.getSecond();

            // extract the op part and store the remainder in rest2
            Pair<ASTExpression, String> rightres = parseExpr(rest2);
            ASTExpression right = rightres.getFirst();
            String rest3 = rightres.getSecond();
            return new Pair<ASTExpression, String>(new ArithmeticExpression(left, op, right), rest3);
        }
//        } else if (input.startsWith("^")) {
//            // Parse method invocation
//            return parseMethodInvocation(input);
//        } else if (input.startsWith("&")) {
//            // Parse field read
//            return parseFieldRead(input);
//        }
        // Handle other cases or throw an error
        throw new IllegalArgumentException("Invalid expression: " + input);
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
