package io.github.pranavgade20.classexplorer;

import java.io.DataOutput;
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

    public void write(DataOutput output) throws IOException {
        for (BytecodeInstruction instruction : bytecode) {
            instruction.write(output);
        }
    }
}

