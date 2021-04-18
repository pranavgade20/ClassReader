import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConstantField {
    ConstantType type; // the tag denoting type of this field
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
                throw new AssertionError("Class file is invalid");
        }
    }

}
class ConstantClassInfo extends ConstantField {
    //represents CONSTANT_Class_info
    public short name_index; // TODO this points to an entry in the constants pool
    ConstantClassInfo(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Class_info;
        name_index = classStream.readShort();
    }

    boolean validate() {
        return klass.constantPool[name_index].type == ConstantType.CONSTANT_Utf8;
    }

    String getName() {
        return ((ConstantUtf8)klass.constantPool[name_index]).value;
    }

    @Override
    public String toString() {
        return getName();
    }
}
class ConstantFieldref extends ConstantField {
    //represents CONSTANT_Fieldref_info
    public short class_index, name_and_type_index;
    ConstantFieldref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Fieldref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    boolean validate() {
        //TODO validate class index
        return klass.constantPool[name_and_type_index].type == ConstantType.CONSTANT_NameAndType && klass.constantPool[class_index].type == ConstantType.CONSTANT_Class_info;
    }
}
class ConstantMethodref extends ConstantField {
    //represents CONSTANT_Methodref_info
    public short class_index, name_and_type_index;
    ConstantMethodref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Methodref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }
}
class ConstantInterfaceMethodref extends ConstantField {
    //represents CONSTANT_InterfaceMethodref_info
    public short class_index, name_and_type_index;
    ConstantInterfaceMethodref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_InterfaceMethodref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }
}
class ConstantString extends ConstantField {
    //represents CONSTANT_String_info
    public short string_index;
    ConstantString(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_String;
        string_index = classStream.readShort();
    }

    boolean validate() {
        return klass.constantPool[string_index].type == ConstantType.CONSTANT_Utf8;
    }

    String getValue() {
        return ((ConstantUtf8)klass.constantPool[string_index]).value;
    }
}
class ConstantInteger extends ConstantField {
    //represents CONSTANT_Integer_info
    public int value;
    ConstantInteger(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Integer;
        value = classStream.readInt();
    }

    boolean validate() {
        return true;
    }

    int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }
}
class ConstantFloat extends ConstantField {
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
}
class ConstantLong extends ConstantField {
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
}
class ConstantDouble extends ConstantField {
    //represents CONSTANT_Double_info
    public double value;
    ConstantDouble(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Double;
        value = classStream.readDouble(); //TODO does this work
    }

    boolean validate() {
        return true;
    }

    double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }
}
class ConstantNameAndType extends ConstantField {
    //represents CONSTANT_NameAndType_info
    public short name_index, descriptor_index;
    ConstantNameAndType(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_NameAndType;
        name_index = classStream.readShort();
        descriptor_index = classStream.readShort();
    }

    void validate() {
        if (klass.constantPool[name_index].type != ConstantType.CONSTANT_Utf8)
            throw new AssertionError("entry in constant pool is invalid.");
        if (klass.constantPool[descriptor_index].type != ConstantType.CONSTANT_Utf8)
            throw new AssertionError("entry in constant pool is invalid.");
    }

    String getName() {
        return ((ConstantUtf8)klass.constantPool[name_index]).value;
    }
    String getDescriptor() {
        return ((ConstantUtf8)klass.constantPool[descriptor_index]).value;
    }

    @Override
    public String toString() {
        return getName() + " " + getDescriptor();
    }
}
class ConstantUtf8 extends ConstantField {
    //represents CONSTANT_Utf8_info
    private short len;
    public String value;
    ConstantUtf8(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Utf8;
        len = classStream.readShort();
        byte[] bytes = new byte[len];
        classStream.readFully(bytes);
        value = new String(bytes, StandardCharsets.UTF_8);
    }

    void validate() {
        if (value == null) throw new AssertionError("string constant is null");
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
class ConstantMethodHandle extends ConstantField {
    //represents CONSTANT_MethodHandle_info
    public byte reference_kind;
    public short reference_index;
    ConstantMethodHandle(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodHandle;
        reference_kind = classStream.readByte();
        reference_index = classStream.readShort();
    }
}
class ConstantMethodType extends ConstantField {
    //represents CONSTANT_MethodType_info
    public short descriptor_index;
    ConstantMethodType(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodType;
        descriptor_index = classStream.readShort();
    }
}
class ConstantInvokeDynamic extends ConstantField {
    //represents CONSTANT_InvokeDynamic_info
    public short bootstrap_method_attr_index, name_and_type_index; // TODO bootstrap_method_attr_index is reference into bootstrap methods table
    ConstantInvokeDynamic(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_InvokeDynamic;
        bootstrap_method_attr_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }
}

enum ConstantType {
    CONSTANT_Utf8(1),
    CONSTANT_Unicode(2),
    CONSTANT_Integer(3),
    CONSTANT_Float(4),
    CONSTANT_Long(5),
    CONSTANT_Double(6),
    CONSTANT_Class_info(7),
    CONSTANT_String(8),
    CONSTANT_Fieldref(9),
    CONSTANT_Methodref(10),
    CONSTANT_InterfaceMethodref(11),
    CONSTANT_NameAndType(12),
    CONSTANT_MethodHandle(15),
    CONSTANT_MethodType(16),
    CONSTANT_InvokeDynamic(18);

    public final int value;
    ConstantType(int num) {
        value = num;
    }
    static ConstantType getConstant(int num) {
        switch (num) {
            case 1: return CONSTANT_Utf8;
            case 3: return CONSTANT_Integer;
            case 4: return CONSTANT_Float;
            case 5: return CONSTANT_Long;
            case 6: return CONSTANT_Double;
            case 7: return CONSTANT_Class_info;
            case 8: return CONSTANT_String;
            case 9: return CONSTANT_Fieldref;
            case 10: return CONSTANT_Methodref;
            case 11: return CONSTANT_InterfaceMethodref;
            case 12: return CONSTANT_NameAndType;
            case 15: return CONSTANT_MethodHandle;
            case 16: return CONSTANT_MethodType;
            case 18: return CONSTANT_InvokeDynamic;
        }
        return null;
    }
}
