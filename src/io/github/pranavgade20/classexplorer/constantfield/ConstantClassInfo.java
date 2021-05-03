package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantClassInfo extends ConstantField {
    //represents CONSTANT_Class_info
    private short name_index;
    public ConstantUtf8 name;

    ConstantClassInfo(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Class_info;
        name_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        name = (ConstantUtf8) klass.constantPool[name_index];
        name_index = -1; // mark as invalid
    }

    String getName() {
        return ((ConstantUtf8) klass.constantPool[name_index]).value;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.name.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.name);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }
}
