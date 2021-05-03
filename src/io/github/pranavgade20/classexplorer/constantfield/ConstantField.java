package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantField {
    public ConstantType type; // the tag denoting type of this field
    Klass klass;

    public static ConstantField getConstantField(DataInput classStream, Klass klass) throws AssertionError, IOException {
        switch (ConstantType.getConstant(classStream.readByte())) {
            case CONSTANT_Class_info:
                return new ConstantClassInfo(classStream, klass);
            case CONSTANT_Fieldref:
                return new ConstantFieldref(classStream, klass);
            case CONSTANT_Methodref:
                return new ConstantMethodref(classStream, klass);
            case CONSTANT_InterfaceMethodref:
                return new ConstantInterfaceMethodref(classStream, klass);
            case CONSTANT_String:
                return new ConstantString(classStream, klass);
            case CONSTANT_Integer:
                return new ConstantInteger(classStream, klass);
            case CONSTANT_Float:
                return new ConstantFloat(classStream, klass);
            case CONSTANT_Long:
                return new ConstantLong(classStream, klass);
            case CONSTANT_Double:
                return new ConstantDouble(classStream, klass);
            case CONSTANT_NameAndType:
                return new ConstantNameAndType(classStream, klass);
            case CONSTANT_Utf8:
                return new ConstantUtf8(classStream, klass);
            case CONSTANT_MethodHandle:
                return new ConstantMethodHandle(classStream, klass);
            case CONSTANT_MethodType:
                return new ConstantMethodType(classStream, klass);
            case CONSTANT_InvokeDynamic:
                return new ConstantInvokeDynamic(classStream, klass);
            default:
                throw new ClassFormatError("Invalid constant type");
        }
    }

    public void resolve(Klass klass) {
    }

    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        output.writeByte(type.value);
    }
}