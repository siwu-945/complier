package ValueNumbering;

import BasicBlock.BasicBlock;
import Primitives.IRAssignment;
import Primitives.IRStatement;
import Primitives.IRVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ValueNumber {
    public void basicValueNumbering(BasicBlock currentBlock) {
        Map<String, Integer> ValueNumber = new HashMap<String, Integer>();
        Map<Integer, IRVariable> name = new HashMap<Integer, IRVariable>();
        AtomicInteger nextVN = new AtomicInteger(0);

        for (int i = 0; i < currentBlock.getIRStatements().size(); i++) {
            IRStatement statement = currentBlock.getIRStatements().get(i);
            if (isArithmeticOperation(statement)) {
                IRAssignment assignment = (IRAssignment) statement;
                String expression = assignment.getRight();
                IRVariable Ti = assignment.getLeft();
                String[] parts = expression.split(" ");
                String left = parts[0];
                String right = parts[2];
                String op = parts[1];

                Integer Vli = ValueNumber.computeIfAbsent(left, k -> nextVN.getAndIncrement());
                Integer Vri = ValueNumber.computeIfAbsent(right, k -> nextVN.getAndIncrement());
                String H = hashExp(op, Vli, Vri);
                if (ValueNumber.containsKey(H)) {
                    IRVariable tempVar = name.get(ValueNumber.get(H));
                    currentBlock.getIRStatements().set(i, new IRAssignment(assignment.getLeft(), tempVar.toString()));
                }
                else {
                    name.put(nextVN.get(), Ti);
                    ValueNumber.put(Ti.toString(), nextVN.get());
                    ValueNumber.put(H, nextVN.getAndIncrement());
                }
            }
            else if (isNumber(statement)) {
                IRAssignment assignment = (IRAssignment) statement;
                String numExp = ((IRAssignment) statement).getRight();
                IRVariable Ti = assignment.getLeft();
                int num = Integer.parseInt(numExp);
                String H = String.valueOf(Objects.hash(num));
                if (ValueNumber.containsKey(H)) {
                    IRVariable tempVar = name.get(ValueNumber.get(H));
                    currentBlock.getIRStatements().set(i, new IRAssignment(assignment.getLeft(), tempVar.toString()));
                }
                else {
                    name.put(nextVN.get(), Ti);
                    ValueNumber.put(Ti.toString(), nextVN.get());
                    ValueNumber.put(H, nextVN.getAndIncrement());
                }
            }
        }
    }

    private String hashExp(String op, Integer vli, Integer vri) {
        return String.valueOf(Objects.hash(op, vli, vri));
    }


    public boolean isArithmeticOperation(IRStatement statement) {
        if (statement instanceof IRAssignment) {
            String expression = ((IRAssignment) statement).getRight();
            if (expression.contains("+") || expression.contains("-") || expression.contains("*") || expression.contains("/") || expression.contains("|") || expression.contains("==") || expression.contains("!=") || expression.contains("<=") || expression.contains(">=")) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumber(IRStatement statement) {
        if (statement instanceof IRAssignment) {
            String expression = ((IRAssignment) statement).getRight();
            if (expression.matches("-?\\d+(\\.\\d+)?")) {
                return true;
            }
        }
        return false;
    }
}

