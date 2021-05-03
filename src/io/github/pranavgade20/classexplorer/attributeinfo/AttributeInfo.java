package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.AccessFlags;
import io.github.pranavgade20.classexplorer.JavaBytecode;
import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class AttributeInfo {
    public ConstantUtf8 attribute_name;
    public int attribute_length;
    public byte[] info = new byte[0];
    AttributeInfo(DataInput classStream, Klass klass) throws IOException {
        attribute_name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
        attribute_length = classStream.readInt();
    }

    AttributeInfo(AttributeInfo from, DataInput classStream, Klass klass) throws IOException {
        attribute_name = from.attribute_name;
        attribute_length = from.attribute_length;
        info = new byte[attribute_length];
        classStream.readFully(info);
    }

    AttributeInfo(AttributeInfo from) {
        attribute_name = from.attribute_name;
        attribute_length = from.attribute_length;
        info = from.info;
    }

    public static AttributeInfo getAttributeInfo(DataInput classStream, Klass klass) throws IOException {
        AttributeInfo info = new AttributeInfo(classStream, klass);
        switch (info.attribute_name.value) {
            case "ConstantValue": return new ConstantValueAttribute(info, classStream, klass);
            case "Code": return new CodeAttribute(info, classStream, klass);
            case "Exceptions": return new ExceptionsAttribute(info, classStream, klass);
            case "InnerClasses": return new InnerClassesAttribute(info, classStream, klass);
            case "EnclosingMethod": return new EnclosingMethodAttribute(info, classStream, klass);
            case "Synthetic": return new SyntheticAttribute(info, classStream, klass);
            case "Signature": return new SignatureAttribute(info, classStream, klass);
            case "SourceFile": return new SourceFileAttribute(info, classStream, klass);
            case "SourceDebugExtension": return new SourceDebugExtensionAttribute(info, classStream, klass);
            case "LineNumberTable": return new LineNumberTableAttribute(info, classStream, klass);
            case "LocalVariableTable": return new LocalVariableTableAttribute(info, classStream, klass);
            case "LocalVariableTypeTable": return new LocalVariableTypeTableAttribute(info, classStream, klass);
            case "Deprecated": return new DeprecatedAttribute(info, classStream, klass);
            case "RuntimeVisibleAnnotations": return new RuntimeVisibleAnnotationsAttribute(info, classStream, klass);
            case "RuntimeInvisibleAnnotations": return new RuntimeInvisibleAnnotationsAttribute(info, classStream, klass);
            case "RuntimeVisibleParameterAnnotations": return new RuntimeVisibleParameterAnnotationsAttribute(info, classStream, klass);
            case "RuntimeInvisibleParameterAnnotations": return new RuntimeInvisibleParameterAnnotationsAttribute(info , classStream, klass);
            case "AnnotationDefault": return new AnnotationDefaultAttribute(info, classStream, klass);
            case "BootstrapMethods": return new BootstrapMethodsAttribute(info, classStream, klass);
            case "StackMapTable": //TODO implement
            default: return new AttributeInfo(info, classStream, klass);
        }
    }

    @Override
    public String toString() {
        return "name=" + attribute_name;
    }

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
        output.writeShort(idx+1);
        output.writeInt(info.length);
        output.write(info);
    }
}

