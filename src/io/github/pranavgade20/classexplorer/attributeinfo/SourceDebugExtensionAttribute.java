package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class SourceDebugExtensionAttribute extends AttributeInfo {
    // represents SourceDebugExtension_attribute
    byte[] debug_extension;

    SourceDebugExtensionAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        debug_extension = new byte[attribute_length];
        classStream.readFully(debug_extension);
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

        output.writeInt(debug_extension.length);
        output.write(debug_extension);
    }
}
