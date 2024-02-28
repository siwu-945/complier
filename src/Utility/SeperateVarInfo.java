package Utility;

import Types.Type;

public class SeperateVarInfo {

    public static String seperateName(String varInfo) {
        int sepIndex = varInfo.indexOf(":");
        String varName = varInfo.substring(0, sepIndex);
        return varName;
    }

    public static Type seperateType(String varInfo) {
        int sepIndex = varInfo.indexOf(":");
        String varTypeString = varInfo.substring(sepIndex + 1);
        Type varType = StringToType.toType(varTypeString, null);
        return varType;
    }
}
