package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantFieldref extends ConstantField {
    //represents CONSTANT_Fieldref_info
    private short class_index, name_and_type_index;
    public ConstantClassInfo classs;
    public ConstantNameAndType name_and_type;

    ConstantFieldref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Fieldref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        classs = (ConstantClassInfo) klass.constantPool[class_index];
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        class_index = -1; // mark invalid
        name_and_type_index = -1; // mark invalid
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.classs.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.classs);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);

        idx = 0;
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
