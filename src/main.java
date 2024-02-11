import BasicBlock.BasicBlock;
import Primitives.IRStatement;
import Primitives.TransformIR;

import java.util.ArrayList;
import java.util.HashMap;

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
                "    fields y\n" +
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
        HashMap<String, BasicBlock> blocks = myParser.readingSource(wholeSource);
        System.out.println(blocks.get("main").getName());

    }
}