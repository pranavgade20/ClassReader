package io.github.pranavgade20.classexplorer.constantfield;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class ConstantMethodHandle extends ConstantField {
    //represents CONSTANT_MethodHandle_info
    private short reference_index;
    public ReferenceKind reference_kind;
    public ConstantField reference;

    ConstantMethodHandle(DataInput classStream, Klass klass) throws IOException {
        this.klass = klass;
        type = ConstantType.CONSTANT_MethodHandle;
        reference_kind = ReferenceKind.values()[classStream.readByte() - 1];
        reference_index = classStream.readShort();
    }

    @Override
    public void resolve(Klass klass) {
        reference = klass.constantPool[reference_index];
        reference_index = -1; // mark invalid
    }

    @Override
    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        super.write(output, constant_pool);

        output.write(reference_kind.value);
        int idx = 0;
        for (int i = 0; i < constant_pool.size(); i++) {
            if (this.reference.equals(constant_pool.get(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0) {
            constant_pool.add(this.reference);
            idx = constant_pool.size();
        }
        output.writeShort(idx + 1);
    }

    enum ReferenceKind {
        REF_getField((byte) 1),
        REF_getStatic((byte) 2),
        REF_putField((byte) 3),
        REF_putStatic((byte) 4),
        REF_invokeVirtual((byte) 5),
        REF_invokeStatic((byte) 6),
        REF_invokeSpecial((byte) 7),
        REF_newInvokeSpecial((byte) 8),
        REF_invokeInterface((byte) 9);

        byte value;

        ReferenceKind(byte value) {
            this.value = value;
        }
    }
}
