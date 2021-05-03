package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;
import io.github.pranavgade20.classexplorer.constantfield.ConstantUtf8;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LocalVariableTableAttribute extends AttributeInfo {
    // represents LocalVariableTable_attribute
    short local_variable_table_length;
    LinkedList<LocalVariableTableEntry> local_variable_table;

    LocalVariableTableAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        local_variable_table_length = classStream.readShort();

        local_variable_table = new LinkedList<>();
        for (int i = 0; i < local_variable_table_length; i++) {
            local_variable_table.add(i, new LocalVariableTableEntry(classStream, klass));
        }
    }

    public class LocalVariableTableEntry {
        short start_pc, length, index;
        ConstantUtf8 name, descriptor;

        LocalVariableTableEntry(DataInput classStream, Klass klass) throws IOException {
            start_pc = classStream.readShort();
            length = classStream.readShort();
            name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
            descriptor = (ConstantUtf8) klass.constantPool[classStream.readShort()];
            index = classStream.readShort();
        }

        public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
            output.writeShort(start_pc);
            output.writeShort(length);

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

            idx = 0;
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

            output.writeShort(index);
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

        output.writeInt(local_variable_table.size() * 10 + 2);

        output.writeShort(local_variable_table.size());
        for (LocalVariableTableEntry entry : local_variable_table) {
            entry.write(output, constant_pool);
        }
    }
}
