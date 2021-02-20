import java.io.DataInput;
import java.io.IOException;

public class AttributeInfo {
    public ConstantUtf8 attribute_name;
    public int attribute_length;
    public byte[] info;
    AttributeInfo(DataInput classStream, Klass klass) throws IOException {
        attribute_name = (ConstantUtf8) klass.constantPool[classStream.readShort()];
        attribute_length = classStream.readInt();
        info = new byte[attribute_length];
        classStream.readFully(info);
    }

    @Override
    public String toString() {
        return "name=" + attribute_name;
    }
}
