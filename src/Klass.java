import java.io.*;

public class Klass {
    public short major_version, minor_version;
    public short constant_pool_count, interfaces_count, fields_count, methods_count, attributes_count;
    public ConstantField constantPool[];
    public AccessFlags access_flags;
    public ConstantClass this_class, super_class;
    public ConstantClass interfaces[];
    public FieldInfo fields[];
    public MethodInfo methods[];
    public AttributeInfo attributes[];

    Klass(Class c) throws AssertionError, IOException {
        DataInput classStream = new DataInputStream(c.getResourceAsStream(c.getSimpleName()+".class"));
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

        access_flags = AccessFlags.fromFlags(classStream.readShort());
        try {
            this_class = (ConstantClass) constantPool[classStream.readShort()];
            super_class = (ConstantClass) constantPool[classStream.readShort()];
        } catch (Exception e) {
            throw new AssertionError("value points to incorrect entry in constant pool");
        }

        interfaces_count = classStream.readShort();
        interfaces = new ConstantClass[interfaces_count];
        for (int i = 0; i < interfaces_count; i++) {
            try {
                interfaces[i] = (ConstantClass) constantPool[classStream.readShort()];
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
            attributes[i] = new AttributeInfo(classStream, this);
        }

        try {
            classStream.readByte();
        } catch (EOFException e) {
            return;
        }

        throw new AssertionError("Class file is longer than expected");
    }

//    public InputStream getKlass() {
//        return new InputStream() {
//            int pos;
//            @Override
//            public int read() throws IOException {
//                int ret = -1;
//                if (pos < 4)
//                    ret = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe}[pos];
//                else if (pos == 5) ret = (minor_version & 0xFF00)>>8;
//                else if (pos == 6) ret = (minor_version & 0xFF);
//                else if (pos == 7) ret = (major_version & 0xFF00)>>8;
//                else if (pos == 8) ret = (major_version & 0xFF);
//                else pos--;
//
//                pos++;
//                return ret;
//            }
//        };
//    }
}
