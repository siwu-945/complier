import BasicBlock.BasicBlock;
import Primitives.IRStatement;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class main {
    private static String generateFieldParameter(int[] field) {
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


    private static ArrayList<String> getBlockNames(Map<String, BasicBlock> blocks) {
        ArrayList<String> blockNames = new ArrayList<>();
        blocks.forEach((key, value) -> {
            blockNames.add(key);
        });
        return blockNames;
    }

    private static String generateMethodParameter(String[] field) {
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

    private static ArrayList<String> getTbleNames(Map<String, BasicBlock> blocks) {
        ArrayList<String> vtbleNames = new ArrayList<>();

        blocks.forEach((key, value) -> {
            if (!key.equals("main") && value.getAttribute().equals("class")) {
                vtbleNames.add(key);
            }
        });

        return vtbleNames;
    }

    private static ArrayList<String> generateGlobalVtbleArray(ArrayList<String> vtbleNames, Map<String, ArrayList<String>> totalMethods) {
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

    private static ArrayList<String> generateGlobalFieldArray(ArrayList<String> vtbleNames, Map<String, ArrayList<String>> totalFields) {
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

    private static ArrayList<String> generateGlobalMethodString(ArrayList<String> globalVtbleArray, Map<String, ArrayList<String>> totalMethods, ArrayList<String> vtbleNames) {
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

    private static ArrayList<String> generateGlobalFieldArrayString(ArrayList<String> globalArray, Map<String, ArrayList<String>> totalFields, ArrayList<String> vtbleNames) {
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

    public static void main(String[] args) {
        String wholeSource = "";
        String filePath = "";

        if (args.length != 1) {
            filePath = "example1.txt";
        }
        else {
            filePath = args[0];
        }
        File file = new File(filePath);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                wholeSource += scanner.nextLine() + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Parser myParser = new Parser();

        Map<String, BasicBlock> blocks = myParser.readingSource(wholeSource);

        Map<String, ArrayList<String>> totalFields = myParser.generateFields(wholeSource);
        Map<String, ArrayList<String>> totalMethods = myParser.generateMethods(wholeSource);


        ArrayList<String> vtbleNames = getTbleNames(blocks);
        ArrayList<String> globalFieldArray = generateGlobalFieldArray(vtbleNames, totalFields);
        ArrayList<String> fieldArrayStrings = generateGlobalFieldArrayString(globalFieldArray, totalFields, vtbleNames);

        ArrayList<String> globalVtbleArray = generateGlobalVtbleArray(vtbleNames, totalMethods);
        ArrayList<String> vtbleArrayString = generateGlobalMethodString(globalVtbleArray, totalMethods, vtbleNames);
        // print global array
        for (String arrayString : fieldArrayStrings) {
            System.out.println(arrayString);
        }

        for (String methodString : vtbleArrayString) {
            System.out.println(methodString);
        }

        //print main
        ArrayList<String> blockNames = getBlockNames(blocks);

        for (String blockName : blockNames) {
            ArrayList<IRStatement> blockIR = blocks.get(blockName).getIRStatements();
            System.out.println(blockName + ":");
            for (IRStatement statement : blockIR) {
                System.out.println("    " + statement);
            }
        }
    }


}