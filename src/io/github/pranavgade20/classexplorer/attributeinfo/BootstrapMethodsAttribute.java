package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;
import io.github.pranavgade20.classexplorer.constantfield.ConstantField;
import io.github.pranavgade20.classexplorer.constantfield.ConstantMethodHandle;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BootstrapMethodsAttribute extends AttributeInfo {
    // represents BootstrapMethods_attribute
    short num_bootstrap_methods;
    LinkedList<BootstrapMethod> bootstrap_methods;

    BootstrapMethodsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        num_bootstrap_methods = classStream.readShort();
        bootstrap_methods = new LinkedList<>();
        for (int i = 0; i < num_bootstrap_methods; i++) {
            bootstrap_methods.add(i, new BootstrapMethod(classStream, klass));
        }
    }

    public class BootstrapMethod {
        ConstantMethodHandle bootstrap_method;
        short num_bootstrap_arguments;
        ConstantField[] bootstrap_arguments;

        BootstrapMethod(DataInput classStream, Klass klass) throws IOException {
            bootstrap_method = (ConstantMethodHandle) klass.constantPool[classStream.readShort()];
            num_bootstrap_arguments = classStream.readShort();
            bootstrap_arguments = new ConstantField[num_bootstrap_arguments];

            for (int i = 0; i < num_bootstrap_arguments; i++) {
                bootstrap_arguments[i] = klass.constantPool[classStream.readShort()];
            }
        }

        public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
            int idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.bootstrap_method.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.bootstrap_method);
                idx = constant_pool.size();
            }
            output.writeShort(idx + 1);

            output.writeInt(bootstrap_arguments.length);

            for (ConstantField argument : bootstrap_arguments) {
                idx = 0;
                for (int i = 0; i < constant_pool.size(); i++) {
                    if (argument.equals(constant_pool.get(i))) {
                        idx = i;
                        break;
                    }
                }
                if (idx == 0) {
                    constant_pool.add(argument);
                    idx = constant_pool.size();
                }
                output.writeShort(idx + 1);
            }
        }
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
        output.writeShort(idx + 1);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutput data = new DataOutputStream(outStream);

        for (BootstrapMethod method : bootstrap_methods) {
            method.write(data, constant_pool);
        }

        output.writeInt(outStream.size() + 2);
        output.writeShort(bootstrap_methods.size());
        outStream.writeTo((OutputStream) output);
    }
}
