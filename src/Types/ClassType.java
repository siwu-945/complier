package Types;

import AST.ASTExpression;

public class ClassType extends Type {
    ASTExpression className;

    public ClassType(ASTExpression className) {
        this.className = className;
    }

    public ASTExpression getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "ClassType " + className;
    }
}
