package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.AccessFlags;
import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantClassInfo;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;
import io.github.pranavgade20.classexplorer.constantfield.ConstantUtf8;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InnerClassesAttribute extends AttributeInfo {
    // represents InnerClasses_attribute
    short number_of_classes;
    LinkedList<ClassTableEntry> classes;

    InnerClassesAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        number_of_classes = classStream.readShort();
        classes = new LinkedList<>();
        for (int i = 0; i < number_of_classes; i++) {
            classes.add(i, new ClassTableEntry(classStream, klass));
        }
    }

    public class ClassTableEntry {
        ConstantClassInfo inner_class_info, outer_class_info;
        ConstantUtf8 inner_name;
        AccessFlags inner_class_access_flags;

        ClassTableEntry(DataInput classStream, Klass klass) throws IOException {
            inner_class_info = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
            outer_class_info = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
            inner_name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
            inner_class_access_flags = new AccessFlags(classStream.readShort());
        }

        public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
            int idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.inner_class_info.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.inner_class_info);
                idx = constant_pool.size();
            }
            output.writeShort(idx + 1);

            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.outer_class_info.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.outer_class_info);
                idx = constant_pool.size();
            }
            output.writeShort(idx + 1);

            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.inner_name.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.inner_name);
                idx = constant_pool.size();
            }
            output.writeShort(idx + 1);

            output.writeShort(inner_class_access_flags.getFlags());
        }

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

        output.writeInt(classes.size() * 8 + 2);

        for (ClassTableEntry entry : classes) {
            entry.write(output, constant_pool);
        }
    }
}
