package Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalDataSegment {
    Map<String, GlobalArray> globalArrays;

    public GlobalDataSegment() {
        this.globalArrays = new HashMap<>();
    }

    public void declareArray(String name, ArrayList<String> values) {
        globalArrays.put(name, new GlobalArray(name, values));
    }

    public GlobalArray getArray(String name) {
        return globalArrays.get(name);
    }
}
