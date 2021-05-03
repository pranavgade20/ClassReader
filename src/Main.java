import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Klass klass = new Klass(MainTest.class);
        System.out.println("Version: " + klass.major_version + "." + klass.minor_version);

        klass.write(new DataOutputStream(new FileOutputStream("MainTest.class")));

        Klass klass1 = new Klass(new FileInputStream("MainTest.class"));
        System.out.println("Version: " + klass1.major_version + "." + klass1.minor_version);
    }
}

class TestClass {
//    int x = 69;
//    long yx = 0xCAFEBABECAFEBABEL;

//    public int getX() {
//        return x;
//    }
//    public int get2X() {
//        return x*2;
//    }
}