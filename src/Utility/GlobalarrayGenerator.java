package Utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalarrayGenerator {

    public static String generateFieldParameter(int[] field) {
        String para = "";
        for (int i = 0; i < field.length; i++) {
            if (i != field.length - 1) {
                para += field[i] + " , ";
            }
            else {
                para += field[i];
            }
        }
        return para;
    }

    public static String generateMethodParameter(String[] field) {
        String para = "";
        for (int i = 0; i < field.length; i++) {
            if (i != field.length - 1) {
                para += field[i] + " , ";
            }
            else {
                para += field[i];
            }
        }
        return para;
    }

    public static ArrayList<String> generateGlobalFieldArray(ArrayList<String> vtbleNames, Map<String, ArrayList<String>> totalFields) {
        ArrayList<String> globalArray = new ArrayList<>();
        for (String tbleName : vtbleNames) {
            ArrayList<String> fields = totalFields.get(tbleName);
            for (int i = 2; i < fields.size(); i++) {
                if (!globalArray.contains(fields.get(i))) {
                    globalArray.add(fields.get(i));
                }
            }
        }
        return globalArray;
    }

    public static ArrayList<String> generateGlobalFieldArrayString(ArrayList<String> globalArray, Map<String, ArrayList<String>> totalFields, ArrayList<String> vtbleNames) {
        ArrayList globalArrayList = new ArrayList();
        int fieldSize = globalArray.size();
        int[] fieldsX = new int[fieldSize];
        for (String tbleName : vtbleNames) {
            ArrayList<String> fields = totalFields.get(tbleName);
            fields.remove(0);
            fields.remove(0);
            for (int i = 0; i < fieldSize; i++) {
                String currentField = globalArray.get(i);
                int fieldID = fields.indexOf(currentField) + 2;
                if (fieldID > 1) {
                    fieldsX[i] = fieldID;
                }
                else {
                    fieldsX[i] = 0;
                }
            }
            globalArrayList.add(String.format("global array vfields%s: { %s }", tbleName, generateFieldParameter(fieldsX)));
            fieldsX = new int[fieldSize];
        }
        return globalArrayList;
    }

    public static ArrayList<String> generateGlobalVtbleArray(ArrayList<String> vtbleNames, Map<String, ArrayList<String>> totalMethods) {
        ArrayList<String> globalVtbleMethod = new ArrayList<>();
        for (String tbleName : vtbleNames) {
            ArrayList<String> methods = totalMethods.get(tbleName);
            for (int i = 0; i < methods.size(); i++) {
                if (!globalVtbleMethod.contains(methods.get(i))) {
                    globalVtbleMethod.add(methods.get(i));
                }
            }
        }
        return globalVtbleMethod;
    }

    public static ArrayList<String> generateGlobalMethodString(ArrayList<String> globalVtbleArray, Map<String, ArrayList<String>> totalMethods, ArrayList<String> vtbleNames) {
        ArrayList globalArrayList = new ArrayList();
        int methodSizze = globalVtbleArray.size();
        String[] methodX = new String[methodSizze];
        for (String tbleName : vtbleNames) {
            ArrayList<String> method = totalMethods.get(tbleName);

            for (int i = 0; i < methodSizze; i++) {
                String currentMethod = globalVtbleArray.get(i);
                int fieldID = method.indexOf(currentMethod);
                if (fieldID > -1) {
                    methodX[i] = currentMethod;
                }
                else {
                    methodX[i] = "0";
                }
            }
            globalArrayList.add(String.format("global array vtble%s: { %s }", tbleName, generateMethodParameter(methodX)));
            methodX = new String[methodSizze];
        }
        return globalArrayList;
    }

    public static Map<String, int[]> fieldsArrayStringByClass(ArrayList<String> globalArray, Map<String, ArrayList<String>> totalFields, ArrayList<String> vtbleNames) {
        Map<String, int[]> fieldsMap = new LinkedHashMap<>();
        int fieldSize = globalArray.size();
        int[] fieldsX = new int[fieldSize];
        for (String tbleName : vtbleNames) {
            ArrayList<String> fields = totalFields.get(tbleName);
            fields.remove(0);
            fields.remove(0);
            for (int i = 0; i < fieldSize; i++) {
                String currentField = globalArray.get(i);
                int fieldID = fields.indexOf(currentField) + 2;
                if (fieldID > 1) {
                    fieldsX[i] = fieldID;
                }
                else {
                    fieldsX[i] = 0;
                }
            }
            fieldsMap.put(tbleName, fieldsX);
            fieldsX = new int[fieldSize];
        }
        return fieldsMap;
    }

}
