package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantMethodType extends ConstantField {
    //represents CONSTANT_MethodType_info
    private short descriptor_index;
    public ConstantUtf8 descriptor;

    ConstantMethodType(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodType;
        descriptor_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        descriptor = (ConstantUtf8) klass.constantPool[descriptor_index];
        descriptor_index = -1; // mark invalid
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.descriptor.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.descriptor);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }
}
