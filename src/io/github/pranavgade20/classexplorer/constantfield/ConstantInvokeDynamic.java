package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantInvokeDynamic extends ConstantField {
    //represents CONSTANT_InvokeDynamic_info
    public short bootstrap_method_attr_index; // TODO bootstrap_method_attr_index is reference into bootstrap methods table
    private short name_and_type_index;
    public ConstantNameAndType name_and_type;

    ConstantInvokeDynamic(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_InvokeDynamic;
        bootstrap_method_attr_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        name_and_type_index = -1; // mark invalid
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        output.writeShort(bootstrap_method_attr_index); // TODO calculate this somehow
        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.name_and_type.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.name_and_type);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }
}
