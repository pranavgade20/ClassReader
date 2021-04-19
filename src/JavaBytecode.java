import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public class JavaBytecode {
    List<BytecodeInstruction> bytecode;
    public JavaBytecode(byte[] code) {
        bytecode = BytecodeInstruction.getInstructionsFromBytes(code);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (BytecodeInstruction instruction : bytecode) {
            ret.append(instruction).append("\n");
        }
        return ret.toString();
    }
}

