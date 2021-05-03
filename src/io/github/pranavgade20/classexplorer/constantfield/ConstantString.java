package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantString extends ConstantField {
    //represents CONSTANT_String_info
    private short string_index;
    public ConstantUtf8 string;

    ConstantString(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_String;
        string_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        string = (ConstantUtf8) klass.constantPool[string_index];
        string_index = -1;
    }

    String getValue() {
        return string.value;
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.string.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.string);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }
}
