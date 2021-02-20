import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Klass klass = new Klass(TestClass.class);
        System.out.println("Version: " + klass.major_version + "." + klass.minor_version);
//        JavaClass javaClass = (new ClassParser(TestClass.class.getResourceAsStream("TestClass.class"), "TestClass.class")).parse();
    }
}

class TestClass {
    int x = 0xCAFEBABE;
    long yx = 0xCAFEBABECAFEBABEL;
}