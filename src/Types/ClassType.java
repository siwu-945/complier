package Types;

import Class.ClassNode;

public class ClassType extends Type {
    ClassNode classInfo;

    public ClassType(ClassNode classInfo) {
        this.classInfo = classInfo;
    }

    public String getClassName() {
        return classInfo.getClassName();
    }

    public ClassNode getClassInfo() {
        return classInfo;
    }

    @Override
    public String toString() {
        return "ClassType " + classInfo.getClassName();
    }

}
