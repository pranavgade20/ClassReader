package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantFloat extends ConstantField {
    //represents CONSTANT_Float_info
    public float value;

    ConstantFloat(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Float;
        value = classStream.readFloat();
    }

    boolean validate() {
        return true;
    }

    float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        output.writeFloat(value);
    }
}
