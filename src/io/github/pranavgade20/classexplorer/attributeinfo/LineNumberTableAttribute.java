package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LineNumberTableAttribute extends AttributeInfo {
    // represents LineNumberTable_attribute
    short line_number_table_length;
    LinkedList<LineNumberTableEntry> line_number_table;

    LineNumberTableAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        line_number_table_length = classStream.readShort();
        line_number_table = new LinkedList<>();
        for (int i = 0; i < line_number_table_length; i++) {
            line_number_table.add(i, new LineNumberTableEntry(classStream));
        }
    }

    public class LineNumberTableEntry {
        short start_pc;
        short line_number;

        LineNumberTableEntry(DataInput classStream) throws IOException {
            start_pc = classStream.readShort();
            line_number = classStream.readShort();
        }

        public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
            output.writeShort(start_pc);
            output.writeShort(line_number);
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

        output.writeInt(line_number_table.size() * 4 + 2);

        output.writeShort(line_number_table.size());

        for (LineNumberTableEntry entry : line_number_table) {
            entry.write(output, constant_pool);
        }
    }
}
