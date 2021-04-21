import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class MethodInfo {
    public AccessFlags access_flags;
    public ConstantUtf8 name, descriptor;
    public short attributes_count;
    public AttributeInfo[] attributes;

    public MethodInfo(DataInput classStream, Klass klass) throws IOException {
        access_flags = AccessFlags.fromFlags(classStream.readShort());
        name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
        descriptor = (ConstantUtf8) klass.constantPool[classStream.readShort()];
        attributes_count = classStream.readShort();

        attributes = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributes_count; i++) {
            attributes[i] = AttributeInfo.getAttributeInfo(classStream, klass);
        }
    }

    @Override
    public String toString() {
        return name + " " + descriptor;
    }

    public void write(DataOutput output, List<ConstantField> constant_pool) throws IOException {
        output.writeShort(access_flags.getFlags());

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

        output.writeShort(attributes.length);
        for (AttributeInfo attribute : attributes) {
            attribute.write(output, constant_pool);
        }
    }
}
