import java.io.*;
import java.nio.ByteBuffer;
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
            case"RuntimeVisibleAnnotations": return new RuntimeVisibleAnnotationsAttribute(info, classStream, klass);
            case"RuntimeInvisibleAnnotations": return new RuntimeInvisibleAnnotationsAttribute(info, classStream, klass);
            case"RuntimeVisibleParameterAnnotations": return new RuntimeVisibleParameterAnnotationsAttribute(info, classStream, klass);
            case"RuntimeInvisibleParameterAnnotations": return new RuntimeInvisibleParameterAnnotationsAttribute(info , classStream, klass);
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

class ConstantValueAttribute extends AttributeInfo {
    // represents ConstantValue_attribute
    ConstantField value;
    ConstantValueAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);
        value = klass.constantPool[classStream.readShort()];

        if(!validate()) throw new ClassCastException("Index into constant pool is incorrect");
    }

    boolean validate() {
        return value.type == ConstantType.CONSTANT_Long ||
                value.type == ConstantType.CONSTANT_Float ||
                value.type == ConstantType.CONSTANT_Double ||
                value.type == ConstantType.CONSTANT_Integer ||
                value.type == ConstantType.CONSTANT_String;
    }

    @Override
    public String toString() {
        return "name=" + attribute_name +
                ", value=" + value;
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
        output.writeShort(idx+1);
        output.writeInt(2);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.value.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.value);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
    }
}
class CodeAttribute extends AttributeInfo {
    // represents Code_attribute
    short max_stack, max_locals, exception_table_length, attributes_count;
    int code_length;
    JavaBytecode code;
    ExceptionTableEntry[] exception_table;
    AttributeInfo[] attributes;
    CodeAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);
        max_stack = classStream.readShort();
        max_locals = classStream.readShort();
        code_length = classStream.readInt();
        byte[] bytecode = new byte[code_length];
        classStream.readFully(bytecode);
        code = new JavaBytecode(bytecode);

        exception_table_length = classStream.readShort();
        exception_table = new ExceptionTableEntry[exception_table_length];
        for (int i = 0; i < exception_table_length; i++) {
            exception_table[i] = new ExceptionTableEntry(classStream);
        }
        attributes_count = classStream.readShort();
        attributes = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributes_count; i++) {
            attributes[i] = AttributeInfo.getAttributeInfo(classStream, klass);
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
        output.writeShort(idx+1);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutput data = new DataOutputStream(outStream);

        code.write(data);
        int code_length = outStream.size();

        data.writeShort(exception_table.length);
        for (ExceptionTableEntry entry : exception_table) entry.write(data);

        data.writeShort(attributes.length);
        for (AttributeInfo attribute : attributes) attribute.write(data, constant_pool);

        output.writeInt(8 + outStream.size());
        output.writeShort(max_stack);
        output.writeShort(max_locals);
        output.writeInt(code_length);
        outStream.writeTo((OutputStream) output);
    }
}

//class StackMapTableAttribute extends AttributeInfo {
//    // represents StackMapTable_attribute
// TODO impl
//}
class ExceptionsAttribute extends AttributeInfo {
    // represents Exceptions_attribute
    short number_of_exceptions;
    ConstantClassInfo[] exception_index_table;
    ExceptionsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        number_of_exceptions = classStream.readShort();
        exception_index_table = new ConstantClassInfo[number_of_exceptions];
        for (int i = 0; i < number_of_exceptions; i++) {
            exception_index_table[i] = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
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
        output.writeShort(idx+1);

        output.writeInt(exception_index_table.length*2 + 2);

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
            output.writeShort(idx+1);
        }
    }
}
class InnerClassesAttribute extends AttributeInfo {
    // represents InnerClasses_attribute
    short number_of_classes;
    ClassTableEntry[] classes;
    InnerClassesAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        number_of_classes = classStream.readShort();
        classes = new ClassTableEntry[number_of_classes];
        for (int i = 0; i < number_of_classes; i++) {
            classes[i] = new ClassTableEntry(classStream, klass);
        }
    }

    public class ClassTableEntry {
        ConstantClassInfo inner_class_info, outer_class_info;
        ConstantUtf8 inner_name;
        AccessFlags inner_class_access_flags;
        ClassTableEntry(DataInput classStream, Klass klass) throws IOException {
            inner_class_info = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
            outer_class_info = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
            inner_name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
            inner_class_access_flags = new AccessFlags(classStream.readShort());
        }

        public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
            int idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.inner_class_info.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.inner_class_info);
                idx = constant_pool.size();
            }
            output.writeShort(idx+1);

            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.outer_class_info.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.outer_class_info);
                idx = constant_pool.size();
            }
            output.writeShort(idx+1);

            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.inner_name.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.inner_name);
                idx = constant_pool.size();
            }
            output.writeShort(idx+1);

            output.writeShort(inner_class_access_flags.getFlags());
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
        output.writeShort(idx+1);

        output.writeInt(classes.length*8 + 2);

        for (ClassTableEntry entry : classes) {
            entry.write(output, constant_pool);
        }
    }
}
class EnclosingMethodAttribute extends AttributeInfo {
    // represents EnclosingMethod_attribute
    ConstantClassInfo enclosing_class; // innermost class that encloses the declaration of the current class
    ConstantNameAndType method = null; // null if the current class is not immediately enclosed by a method or constructor
    EnclosingMethodAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        enclosing_class = (ConstantClassInfo) klass.constantPool[classStream.readShort()];
        short method_index = classStream.readShort();
        if (method_index != 0) method = (ConstantNameAndType) klass.constantPool[method_index];
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
        output.writeShort(idx+1);

        output.writeInt(4);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.enclosing_class.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.enclosing_class);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.method.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.method);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
    }

}
class SyntheticAttribute extends AttributeInfo {
    // represents Synthetic_attribute
    SyntheticAttribute(AttributeInfo parent, DataInput classStream, Klass klass) {
        super(parent);
    }
}
class SignatureAttribute extends AttributeInfo {
    // represents Signature_attribute
    ConstantUtf8 signature;
    SignatureAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        signature = (ConstantUtf8) klass.constantPool[classStream.readShort()];
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
        output.writeShort(idx+1);

        output.writeInt(2);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.signature.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.signature);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
    }
}
class SourceFileAttribute extends AttributeInfo {
    // represents SourceFile_attribute
    ConstantUtf8 sourcefile;
    SourceFileAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        sourcefile = (ConstantUtf8) klass.constantPool[classStream.readShort()];
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
        output.writeShort(idx+1);

        output.writeInt(2);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.sourcefile.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.sourcefile);
            idx = constant_pool.size();
        }
        output.writeShort(idx+1);
    }
}
class SourceDebugExtensionAttribute extends AttributeInfo {
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
        output.writeShort(idx+1);

        output.writeInt(debug_extension.length);
        output.write(debug_extension);
    }
}
class LineNumberTableAttribute extends AttributeInfo {
    // represents LineNumberTable_attribute
    short line_number_table_length;
    LineNumberTableEntry[] line_number_table;
    LineNumberTableAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        line_number_table_length = classStream.readShort();
        line_number_table = new LineNumberTableEntry[line_number_table_length];
        for (int i = 0; i < line_number_table_length; i++) {
            line_number_table[i] = new LineNumberTableEntry(classStream);
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
        output.writeShort(idx+1);

        output.write(line_number_table.length*4 + 2);

        for (LineNumberTableEntry entry : line_number_table) {
            entry.write(output, constant_pool);
        }
    }
}
class LocalVariableTableAttribute extends AttributeInfo {
    // represents LocalVariableTable_attribute
    short local_variable_table_length;
    LocalVariableTableEntry[] local_variable_table;
    LocalVariableTableAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        local_variable_table_length = classStream.readShort();

        local_variable_table = new LocalVariableTableEntry[local_variable_table_length];
        for (int i = 0; i < local_variable_table_length; i++) {
            local_variable_table[i] = new LocalVariableTableEntry(classStream, klass);
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
            output.writeShort(idx+1);

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
            output.writeShort(idx+1);

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
        output.writeShort(idx+1);

        output.writeInt(local_variable_table.length*10 + 2);

        output.writeShort(local_variable_table.length);
        for (LocalVariableTableEntry entry : local_variable_table) {
            entry.write(output, constant_pool);
        }
    }
}
class LocalVariableTypeTableAttribute extends AttributeInfo {
    // represents LocalVariableTypeTable_attribute
    short local_variable_type_table_length;
    LocalVariableTypeTableEntry[] local_variable_type_table;
    LocalVariableTypeTableAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        local_variable_type_table_length = classStream.readShort();

        local_variable_type_table = new LocalVariableTypeTableEntry[local_variable_type_table_length];
        for (int i = 0; i < local_variable_type_table_length; i++) {
            local_variable_type_table[i] = new LocalVariableTypeTableEntry(classStream, klass);
        }
    }

    public class LocalVariableTypeTableEntry {
        short start_pc, length, index;
        ConstantUtf8 name, signature;
        LocalVariableTypeTableEntry(DataInput classStream, Klass klass) throws IOException {
            start_pc = classStream.readShort();
            length = classStream.readShort();
            name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
            signature = (ConstantUtf8) klass.constantPool[classStream.readShort()];
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
            output.writeShort(idx+1);

            idx = 0;
            for (int i = 0; i < constant_pool.size(); i++) {
                if (this.signature.equals(constant_pool.get(i))) {
                    idx = i;
                    break;
                }
            }
            if (idx == 0) {
                constant_pool.add(this.signature);
                idx = constant_pool.size();
            }
            output.writeShort(idx+1);

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
        output.writeShort(idx+1);

        output.writeInt(local_variable_type_table.length*10 + 2);

        output.writeShort(local_variable_type_table.length);
        for (LocalVariableTypeTableEntry entry : local_variable_type_table) {
            entry.write(output, constant_pool);
        }
    }
}
class DeprecatedAttribute extends AttributeInfo {
    // represents Deprecated_attribute
    DeprecatedAttribute(AttributeInfo parent, DataInput classStream, Klass klass) {
        super(parent);
    }
}
class RuntimeVisibleAnnotationsAttribute extends AttributeInfo {
    // represents RuntimeVisibleAnnotations_attribute
    // TODO implement
    byte[] attribute;
    RuntimeVisibleAnnotationsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        attribute = new byte[attribute_length];
        classStream.readFully(attribute);
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
        output.writeShort(idx+1);
        output.writeInt(attribute.length);
        output.write(attribute);
    }
}
class RuntimeInvisibleAnnotationsAttribute extends AttributeInfo {
    // represents RuntimeInvisibleAnnotations_attribute
    // TODO implement
    byte[] attribute;
    RuntimeInvisibleAnnotationsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        attribute = new byte[attribute_length];
        classStream.readFully(attribute);
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
        output.writeShort(idx+1);
        output.writeInt(attribute.length);
        output.write(attribute);
    }
}
class RuntimeVisibleParameterAnnotationsAttribute extends AttributeInfo {
    // represents RuntimeVisibleParameterAnnotations_attribute
    // TODO implement
    byte[] attribute;
    RuntimeVisibleParameterAnnotationsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        attribute = new byte[attribute_length];
        classStream.readFully(attribute);
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
        output.writeShort(idx+1);
        output.writeInt(attribute.length);
        output.write(attribute);
    }
}
class RuntimeInvisibleParameterAnnotationsAttribute extends AttributeInfo {
    // represents RuntimeInvisibleParameterAnnotations_attribute
    // TODO implement
    byte[] attribute;
    RuntimeInvisibleParameterAnnotationsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        attribute = new byte[attribute_length];
        classStream.readFully(attribute);
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
        output.writeShort(idx+1);
        output.writeInt(attribute.length);
        output.write(attribute);
    }
}
class AnnotationDefaultAttribute extends AttributeInfo {
    // represents AnnotationDefault_attribute
    byte[] default_value;
    AnnotationDefaultAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        default_value = new byte[attribute_length];
        classStream.readFully(default_value);
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
        output.writeShort(idx+1);
        output.writeInt(default_value.length);
        output.write(default_value);
    }
}
class BootstrapMethodsAttribute extends AttributeInfo {
    // represents BootstrapMethods_attribute
    short num_bootstrap_methods;
    BootstrapMethod[] bootstrap_methods;
    BootstrapMethodsAttribute(AttributeInfo parent, DataInput classStream, Klass klass) throws IOException {
        super(parent);

        num_bootstrap_methods = classStream.readShort();
        bootstrap_methods = new BootstrapMethod[num_bootstrap_methods];
        for (int i = 0; i < num_bootstrap_methods; i++) {
            bootstrap_methods[i] = new BootstrapMethod(classStream, klass);
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
            output.writeShort(idx+1);

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
                output.writeShort(idx+1);
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
        output.writeShort(idx+1);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutput data = new DataOutputStream(outStream);

        for (BootstrapMethod method : bootstrap_methods) {
            method.write(data, constant_pool);
        }

        output.writeInt(outStream.size() + 2);
        output.writeShort(bootstrap_methods.length);
        outStream.writeTo((OutputStream) output);
    }
}