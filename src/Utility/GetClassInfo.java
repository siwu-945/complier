package Utility;

import Class.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;

import static Utility.Parser.parseClass;

public class GetClassInfo {
    public static HashMap<String, ClassNode> getClassesInfo(ArrayList<String> lines) {
        HashMap<String, ClassNode> classInfo = new HashMap<>();
        IterateSource newIt = new IterateSource();

        int[] classIndex = newIt.findClassStart(lines, 0);
        int currentLine = 0;
        if (classIndex[0] != -999) {
            while (true) {
                if (lines.get(currentLine).startsWith("main ")) {
                    return classInfo;
                }
                String classString = newIt.completeClassString(classIndex, lines);
                ClassNode newClass = parseClass(classString);
                classInfo.put(newClass.getClassName(), newClass);
                currentLine = classIndex[1] + 1;
                classIndex = newIt.findClassStart(lines, currentLine);
                if (classIndex[0] == -999) {
                    currentLine++;
                }
            }
        }
        return classInfo;
    }
}
