import BasicBlock.BasicBlock;
import Primitives.IRStatement;
import Primitives.TransformIR;

import java.util.ArrayList;
import java.util.Map;

public class main {
    public static void main(String[] args) {

        String irTest1 =
                "    y = ((3 + 5) * 6)\n" +
                        " print(3)";

        String irClassTest = "class A [\n" +
                "    fields x\n" +
                "    method m() with locals var1, var2:\n" +
                "      x = (3 + 3)\n" +
                "      y = 5\n" +
                "    method b() with locals var3, var4:\n" +
                "      a = 100000\n" +
                "      b = 80000\n" +
                "]\n" +
                "y = ((3 + 5) * 6)\n" +
                " print(3)";

        String wholeSource = "class A [\n" +
                "    fields x\n" +
                "    method m() with locals:\n" +
                "      return &this.x\n" +
                "]\n" +
                "class B [\n" +
                "    fields a, b, x\n" +
                "    method m() with locals:\n" +
                "      return 0\n" +
                "]\n" +
                "\n" +
                "main with x:\n" +
                "x = @A";
        Parser myParser = new Parser();
        TransformIR myIR = new TransformIR();
        ArrayList<BasicBlock> myBlocks = new ArrayList<BasicBlock>();
        ArrayList<IRStatement> myIRStatements = new ArrayList<>();

//        ClassNode myClass = myParser.parseClass(irClassTest);
//        System.out.println(myClass);
//
//        ArrayList<ASTStatement> codeToStas = myParser.parseStatementBlock(irTest1);
//        GlobalDataSegment classArrays = myParser.checkForClass(codeToStas);
//        myBlocks.add(new BasicBlock(myIRStatements, "initial"));
//        myIR.addToBB(codeToStas, myBlocks, "initial");
//        for (BasicBlock block : myBlocks) {
//            for (IRStatement irLine : block.getIRStatements()) {
//                System.out.println(irLine);
//            }
//        }

        Map<String, BasicBlock> blocks = myParser.readingSource(wholeSource);
        Map<String, ArrayList<String>> totalFields = myParser.generateFields(wholeSource);
        ArrayList<String> vtbleNames = new ArrayList<>();
        ArrayList<String> globalArray = new ArrayList<>();

        blocks.forEach((key, value) -> {
            if (!key.equals("main")) {
                vtbleNames.add(key);
            }
        });

        for (String tbleName : vtbleNames) {
            ArrayList<String> fields = totalFields.get(tbleName);
            for (int i = 2; i < fields.size(); i++) {
                if (!globalArray.contains(fields.get(i))) {
                    globalArray.add(fields.get(i));
                }
            }
        }
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
                } else {
                    fieldsX[i] = 0;
                }
            }
            System.out.println(generateFieldParameter(fieldsX));
            fieldsX = new int[fieldSize];
        }
    }

    private static String generateFieldParameter(int[] field) {
        String para = "";
        for (int i = 0; i < field.length; i++) {
            if (i != field.length - 1) {
                para += field[i] + " , ";
            } else {
                para += field[i];
            }
        }
        return para;
    }
}