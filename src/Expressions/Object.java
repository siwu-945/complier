package Expressions;

import AST.ASTExpression;

public class Object extends ASTExpression {

    String objectName;

    public Object(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String toString() {
        return objectName;
    }
}
