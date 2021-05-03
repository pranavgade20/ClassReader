package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.JavaBytecode;
import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class CodeAttribute extends AttributeInfo {
    // represents Code_attribute
    short max_stack, max_locals, exception_table_length, attributes_count;
    int code_length;
    JavaBytecode code;
    LinkedList<ExceptionTableEntry> exception_table;
    LinkedList<AttributeInfo> attributes;

    CodeAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);
        max_stack = classStream.readShort();
        max_locals = classStream.readShort();
        code_length = classStream.readInt();
        byte[] bytecode = new byte[code_length];
        classStream.readFully(bytecode);
        code = new JavaBytecode(bytecode);

        exception_table_length = classStream.readShort();
        exception_table = new LinkedList<>();
        for (int i = 0; i < exception_table_length; i++) {
            exception_table.add(i, new ExceptionTableEntry(classStream));
        }
        attributes_count = classStream.readShort();
        attributes = new LinkedList<>();
        for (int i = 0; i < attributes_count; i++) {
            attributes.add(i, AttributeInfo.getAttributeInfo(classStream, klass));
        }
    }

    public class ExceptionTableEntry {
        short start_pc, end_pc, handler_pc, catch_type;

        ExceptionTableEntry(DataInput classStream) throws IOException {
            start_pc = classStream.readShort();
            end_pc = classStream.readShort();
            handler_pc = classStream.readShort();
            catch_type = classStream.readShort();
        }

        void write(DataOutput output) throws IOException {
            output.writeShort(start_pc);
            output.writeShort(end_pc);
            output.writeShort(handler_pc);
            output.writeShort(catch_type);
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

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutput data = new DataOutputStream(outStream);

        code.write(data);
        int code_length = outStream.size();

        data.writeShort(exception_table.size());
        for (ExceptionTableEntry entry : exception_table) entry.write(data);

        data.writeShort(attributes.size());
        for (AttributeInfo attribute : attributes) attribute.write(data, constant_pool);

        output.writeInt(8 + outStream.size());
        output.writeShort(max_stack);
        output.writeShort(max_locals);
        output.writeInt(code_length);
        outStream.writeTo((OutputStream) output);
    }
}
