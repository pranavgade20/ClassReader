import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Klass klass = new Klass(TestClass.class);
        System.out.println("Version: " + klass.major_version + "." + klass.minor_version);
    }
}

class TestClass {
    int x = 69;
    long yx = 0xCAFEBABECAFEBABEL;

    public int getX() {
        return x;
    }
    public int get2X() {
        return x*2;
    }
}