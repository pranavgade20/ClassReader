package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantClassInfo;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;
import io.github.pranavgade20.classexplorer.constantfield.ConstantNameAndType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class EnclosingMethodAttribute extends AttributeInfo {
    // represents EnclosingMethod_attribute
    ConstantClassInfo enclosing_class; // innermost class that encloses the declaration of the current class
    ConstantNameAndType method = null; // null if the current class is not immediately enclosed by a method or constructor

    EnclosingMethodAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        enclosing_class = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
        short method_index = classStream.readShort();
        if (method_index != 0) method = (ConstantNameAndType) klass.constantPool[method_index];
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
        output.writeShort(idx + 1);

        output.writeInt(4);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.enclosing_class.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.enclosing_class);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.method.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.method);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }

}
