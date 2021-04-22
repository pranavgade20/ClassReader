import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Klass {
    public short major_version, minor_version;
    public short constant_pool_count, interfaces_count, fields_count, methods_count, attributes_count;
    public ConstantField constantPool[];
    public AccessFlags access_flags;
    public ConstantClassInfo this_class, super_class;
    public ConstantClassInfo interfaces[];
    public FieldInfo fields[];
    public MethodInfo methods[];
    public AttributeInfo attributes[];

    Klass(Class c) throws AssertionError, IOException {
        this(c.getResourceAsStream(c.getSimpleName() + ".class"));
    }

    Klass(InputStream c) throws AssertionError, IOException {
        DataInput classStream = new DataInputStream(c);
        if (classStream.readInt() != 0xCAFEBABE)
            throw new AssertionError("class file magic number mismatch");

        minor_version = classStream.readShort();
        major_version = classStream.readShort();

        constant_pool_count = classStream.readShort();
        constantPool = new ConstantField[constant_pool_count];
        for (int i = 1; i < constant_pool_count; i++) { // i is 1 since constant_pool[0] is not used by compiler
            constantPool[i] = ConstantField.getConstantField(classStream, this);
            // 8-byte constants take up 2 spots in the constant_pool
            if (constantPool[i].type == ConstantType.CONSTANT_Double || constantPool[i].type == ConstantType.CONSTANT_Long) i++;
        }

        for (int i = 0; i < constant_pool_count; i++) {
            if (constantPool[i] == null) continue;
            constantPool[i].resolve(this);
        }

        access_flags = AccessFlags.fromFlags(classStream.readShort());
        try {
            this_class = (ConstantClassInfo) constantPool[classStream.readShort()];
            super_class = (ConstantClassInfo) constantPool[classStream.readShort()];
        } catch (Exception e) {
            throw new AssertionError("value points to incorrect entry in constant pool");
        }

        interfaces_count = classStream.readShort();
        interfaces = new ConstantClassInfo[interfaces_count];
        for (int i = 0; i < interfaces_count; i++) {
            try {
                interfaces[i] = (ConstantClassInfo) constantPool[classStream.readShort()];
            } catch (Exception e) {
                throw new AssertionError("entry in interfaces array is invalid.");
            }
        }

        fields_count = classStream.readShort();
        fields = new FieldInfo[fields_count];
        for (int i = 0; i < fields_count; i++) {
            fields[i] = new FieldInfo(classStream, this);
        }

        methods_count = classStream.readShort();
        methods = new MethodInfo[methods_count];
        for (int i = 0; i < methods_count; i++) {
            methods[i] = new MethodInfo(classStream, this);
        }

        attributes_count = classStream.readShort();
        attributes = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributes_count; i++) {
            attributes[i] = AttributeInfo.getAttributeInfo(classStream, this);
        }

        try {
            classStream.readByte();
        } catch (EOFException e) {
            return;
        }

        throw new AssertionError("Class file is longer than expected");
    }

    public void write(DataOutput output) throws IOException {
        output.writeInt(0xCAFEBABE);

        output.writeShort(minor_version);
        output.writeShort(major_version);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream(); // for constant pool
        DataOutput data = new DataOutputStream(outStream);

        LinkedList<ConstantField> constant_pool = Arrays.stream(constantPool).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedList::new));

        data.writeShort(access_flags.getFlags());

        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.this_class.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.this_class);
            idx = constant_pool.size();
        }
        data.writeShort(idx+1);

        idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.super_class.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.super_class);
            idx = constant_pool.size();
        }
        data.writeShort(idx+1);

        data.writeShort(interfaces.length);
        for (ConstantClassInfo interfaceInfo : interfaces) {
            interfaceInfo.write(data, constant_pool);
        }

        data.writeShort(fields.length);
        for (FieldInfo field : fields) {
            field.write(data, constant_pool);
        }

        data.writeShort(methods.length);
        for (MethodInfo method : methods) {
            method.write(data, constant_pool);
        }

        data.writeShort(attributes.length);
        for (AttributeInfo attribute : attributes) {
            attribute.write(data, constant_pool);
        }

        output.writeShort(constant_pool.size() + 1);

        for (ConstantField constantField : constant_pool) {
//            if (constantField == null) continue;
            constantField.write(output, constant_pool);
        }

        outStream.writeTo((OutputStream) output);
    }
}
