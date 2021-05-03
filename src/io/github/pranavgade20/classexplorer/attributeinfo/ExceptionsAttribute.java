package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantClassInfo;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//class StackMapTableAttribute extends io.github.pranavgade20.classexplorer.attributeinfo.AttributeInfo {
//    // represents StackMapTable_attribute
// TODO impl
//}
public class ExceptionsAttribute extends AttributeInfo {
    // represents Exceptions_attribute
    short number_of_exceptions;
    LinkedList<ConstantClassInfo> exception_index_table;

    ExceptionsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        number_of_exceptions = classStream.readShort();
        exception_index_table = new LinkedList<>();
        for (int i = 0; i < number_of_exceptions; i++) {
            exception_index_table.add(i, (ConstantClassInfo) klass.constantPool[classStream.readShort()]);
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

        output.writeInt(exception_index_table.size() * 2 + 2);

        for (ConstantClassInfo ex : exception_index_table) {
            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (ex.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(ex);
                idx = constant_pool.size();
            }
            output.writeShort(idx + 1);
        }
    }
}
