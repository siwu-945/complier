package ControlTransfer;

public class Jump extends ControlTransfer {
    String blockName;

    public Jump(String blockName) {
        this.blockName = blockName;
    }

    @Override
    public String toString() {
        return "jump " + blockName;
    }
}
