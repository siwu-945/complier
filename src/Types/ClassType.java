package Types;

public class ClassType extends Type {
    String className;

    public ClassType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "ClassType " + className;
    }
}
