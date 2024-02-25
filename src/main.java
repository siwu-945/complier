import BasicBlock.BasicBlock;
import Primitives.IRStatement;
import Utility.GlobalarrayGenerator;
import Utility.IterateSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class main {

    private static ArrayList<String> getBlockNames(Map<String, BasicBlock> blocks) {
        ArrayList<String> blockNames = new ArrayList<>();
        blocks.forEach((key, value) -> {
            blockNames.add(key);
        });
        return blockNames;
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

        Map<String, BasicBlock> blocks = IterateSource.readingSource(wholeSource);

        Map<String, ArrayList<String>> totalFields = IterateSource.generateFields(wholeSource);
        Map<String, ArrayList<String>> totalMethods = IterateSource.generateMethods(wholeSource);


        ArrayList<String> vtbleNames = getTbleNames(blocks);
        ArrayList<String> globalFieldArray = GlobalarrayGenerator.generateGlobalFieldArray(vtbleNames, totalFields);
        ArrayList<String> fieldArrayStrings = GlobalarrayGenerator.generateGlobalFieldArrayString(globalFieldArray, totalFields, vtbleNames);

        ArrayList<String> globalVtbleArray = GlobalarrayGenerator.generateGlobalVtbleArray(vtbleNames, totalMethods);
        ArrayList<String> vtbleArrayString = GlobalarrayGenerator.generateGlobalMethodString(globalVtbleArray, totalMethods, vtbleNames);
        // print global array
        for (String arrayString : fieldArrayStrings) {
            System.out.println(arrayString);
        }

        for (String methodString : vtbleArrayString) {
            System.out.println(methodString);
        }

        ArrayList<String> blockNames = getBlockNames(blocks);

        for (String blockName : blockNames) {
            ArrayList<IRStatement> blockIR = blocks.get(blockName).getIRStatements();
            if (isAClass(blockName, vtbleNames)) {
                System.out.println(blockName + "(this):");
            }
            else {
                System.out.println(blockName + ":");
            }
            for (IRStatement statement : blockIR) {
                System.out.println("    " + statement);
            }
        }
    }

    private static boolean isAClass(String blockName, ArrayList<String> vtbleNames) {
        return vtbleNames.contains(blockName);
    }


}