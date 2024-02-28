package Utility;

import Class.ClassNode;
import Types.ClassType;
import Types.ErrorType;
import Types.Type;

public class StringToType {

    public static Type toType(String returnType, ClassNode classInfo) {
        if (returnType.equals("int")) {
            return new Types.IntType();
        }
        else if (returnType.equals("null")) {
            return new ErrorType("Null Type");
        }
        else {
            return new ClassType(classInfo);
        }

    }
}
