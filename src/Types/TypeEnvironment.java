package Types;

import java.util.HashMap;

public class TypeEnvironment {
    HashMap<String, Type> typeEnv;

    //TODO:
    // Iterate throuogh class, add methods and fields and their corresponding types
    // what if method or field with same name but different types?
    public TypeEnvironment(HashMap<String, Type> typeEnv) {
        this.typeEnv = typeEnv;
    }

    public Type typeLookUp(String name) {
        return typeEnv.get(name);
    }

    public HashMap<String, Type> getTypeEnv() {
        return typeEnv;
    }

    public void storeTypeInfo(String name, Type type) {
        typeEnv.put(name, type);
    }

    public boolean hasClass(String name) {
        return typeEnv.containsKey(name);
    }

}
