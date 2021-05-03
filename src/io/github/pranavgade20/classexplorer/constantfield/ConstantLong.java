package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantLong extends ConstantField {
    //represents CONSTANT_Long_info
    public long value;

    ConstantLong(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Long;
        value = classStream.readLong(); //TODO does this work
    }

    boolean validate() {
        return true;
    }

    long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        output.writeLong(value);
    }
}
