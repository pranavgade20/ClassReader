package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;
import io.github.pranavgade20.classexplorer.constantfield.ConstantType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantValueAttribute extends AttributeInfo {
    // represents ConstantValue_attribute
    ConstantField value;
    ConstantValueAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);
        value = klass.constantPool[classStream.readShort()];

        if(!validate()) throw new ClassCastException("Index into constant pool is incorrect");
    }

    boolean validate() {
        return value.type == ConstantType.CONSTANT_Long ||
                value.type == ConstantType.CONSTANT_Float ||
                value.type == ConstantType.CONSTANT_Double ||
                value.type == ConstantType.CONSTANT_Integer ||
                value.type == ConstantType.CONSTANT_String;
    }

    @Override
    public String toString() {
        return "name=" + attribute_name +
                ", value=" + value;
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.attribute_name.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.attribute_name);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
        output.writeInt(2);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.value.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.value);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
    }
}
