import java.io.DataInput;
import java.io.IOException;

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
            attributes[i] = new AttributeInfo(classStream, klass);
        }
    }

    @Override
    public String toString() {
        return name + " " + descriptor;
    }
}
