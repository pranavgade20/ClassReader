import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Klass klass = new Klass(TestClass.class);
        System.out.println("Version: " + klass.major_version + "." + klass.minor_version);
    }
}

class TestClass {
    short x = 69;
    long yx = 0xCAFEBABECAFEBABEL;

    public int getX() {
        return x;
    }
}