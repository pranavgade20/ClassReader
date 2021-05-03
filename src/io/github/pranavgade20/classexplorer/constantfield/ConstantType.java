package io.github.pranavgade20.classexplorer.constantfield;

public enum ConstantType {
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
            case 1:
                return CONSTANT_Utf8;
            case 3:
                return CONSTANT_Integer;
            case 4:
                return CONSTANT_Float;
            case 5:
                return CONSTANT_Long;
            case 6:
                return CONSTANT_Double;
            case 7:
                return CONSTANT_Class_info;
            case 8:
                return CONSTANT_String;
            case 9:
                return CONSTANT_Fieldref;
            case 10:
                return CONSTANT_Methodref;
            case 11:
                return CONSTANT_InterfaceMethodref;
            case 12:
                return CONSTANT_NameAndType;
            case 15:
                return CONSTANT_MethodHandle;
            case 16:
                return CONSTANT_MethodType;
            case 18:
                return CONSTANT_InvokeDynamic;
        }
        return null;
    }
}
