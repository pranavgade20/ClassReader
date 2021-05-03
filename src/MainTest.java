public class MainTest {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            dd(i);
        }
        System.out.println("Hello, World!");
    }

    static void dd(int n) {
        System.out.println(2*n);
    }
}
