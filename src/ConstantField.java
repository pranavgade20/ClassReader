import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
                throw new AssertionError("Invalid constant type");
        }
    }

    void resolve(Klass klass) {
    }

    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        output.writeByte(type.value);
    }
}
class ConstantClassInfo extends ConstantField {
    //represents CONSTANT_Class_info
    private short name_index;
    public ConstantUtf8 name;
    ConstantClassInfo(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Class_info;
        name_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        name = (ConstantUtf8) klass.constantPool[name_index];
        name_index = -1; // mark as invalid
    }

    String getName() {
        return ((ConstantUtf8)klass.constantPool[name_index]).value;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);
        output.writeShort(name_index);
    }
}
class ConstantFieldref extends ConstantField {
    //represents CONSTANT_Fieldref_info
    private short class_index, name_and_type_index;
    public ConstantClassInfo classs;
    public ConstantNameAndType name_and_type;
    ConstantFieldref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Fieldref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        classs = (ConstantClassInfo) klass.constantPool[class_index];
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        class_index = -1; // mark invalid
        name_and_type_index = -1; // mark invalid
    }
}
class ConstantMethodref extends ConstantField {
    //represents CONSTANT_Methodref_info
    private short class_index, name_and_type_index;
    public ConstantClassInfo classs;
    public ConstantNameAndType name_and_type;
    ConstantMethodref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_Methodref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        classs = (ConstantClassInfo) klass.constantPool[class_index];
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        class_index = -1; // mark invalid
        name_and_type_index = -1; // mark invalid
    }
}
class ConstantInterfaceMethodref extends ConstantField {
    //represents CONSTANT_InterfaceMethodref_info
    private short class_index, name_and_type_index;
    public ConstantClassInfo classs;
    public ConstantNameAndType name_and_type;
    ConstantInterfaceMethodref(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_InterfaceMethodref;
        class_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        classs = (ConstantClassInfo) klass.constantPool[class_index];
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        class_index = -1; // mark invalid
        name_and_type_index = -1; // mark invalid
    }
}
class ConstantString extends ConstantField {
    //represents CONSTANT_String_info
    private short string_index;
    public ConstantUtf8 string;
    ConstantString(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_String;
        string_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        string = (ConstantUtf8) klass.constantPool[string_index];
        string_index = -1;
    }

    String getValue() {
        return string.value;
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
    private short name_index, descriptor_index;
    public ConstantUtf8 name, descriptor;
    ConstantNameAndType(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_NameAndType;
        name_index = classStream.readShort();
        descriptor_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        name = (ConstantUtf8) klass.constantPool[name_index];
        descriptor = (ConstantUtf8) klass.constantPool[descriptor_index];
        name_index = -1; // mark invalid
        descriptor_index = -1; // mark invalid
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

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantUtf8 that = (ConstantUtf8) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
class ConstantMethodHandle extends ConstantField {
    //represents CONSTANT_MethodHandle_info
    private short reference_index;
    public ReferenceKind reference_kind;
    public ConstantField reference;
    ConstantMethodHandle(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodHandle;
        reference_kind = ReferenceKind.values()[classStream.readByte()-1];
        reference_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        reference = klass.constantPool[reference_index];
        reference_index = -1; // mark invalid
    }

    enum ReferenceKind {
        REF_getField((byte)1),
        REF_getStatic((byte)2),
        REF_putField((byte)3),
        REF_putStatic((byte)4),
        REF_invokeVirtual((byte)5),
        REF_invokeStatic((byte)6),
        REF_invokeSpecial((byte)7),
        REF_newInvokeSpecial((byte)8),
        REF_invokeInterface((byte)9);

        byte value;
        ReferenceKind(byte value) {
            this.value = value;
        }
    }
}
class ConstantMethodType extends ConstantField {
    //represents CONSTANT_MethodType_info
    private short descriptor_index;
    public ConstantUtf8 descriptor;
    ConstantMethodType(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodType;
        descriptor_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        descriptor = (ConstantUtf8) klass.constantPool[descriptor_index];
        descriptor_index = -1; // mark invalid
    }
}
class ConstantInvokeDynamic extends ConstantField {
    //represents CONSTANT_InvokeDynamic_info
    public short bootstrap_method_attr_index; // TODO bootstrap_method_attr_index is reference into bootstrap methods table
    private short name_and_type_index;
    public ConstantNameAndType name_and_type;
    ConstantInvokeDynamic(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_InvokeDynamic;
        bootstrap_method_attr_index = classStream.readShort();
        name_and_type_index = classStream.readShort();
    }

    @Override
    void resolve(Klass klass) {
        name_and_type = (ConstantNameAndType) klass.constantPool[name_and_type_index];
        name_and_type_index = -1; // mark invalid
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
