import AST.ASTExpression;

public class main {
    public static void main(String[] args) {
        String code = "((3 + 10) + 3)";

        Parser myParser = new Parser();

        ASTExpression myExp = myParser.parseExpr(code).getFirst();

        System.out.println(myExp);
    }
}