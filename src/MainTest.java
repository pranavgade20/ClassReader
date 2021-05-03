public class MainTest {
    static int abcabc = 23;
    public static void main(String[] args) {
        for (int loop_var = 0; loop_var < 10; loop_var++) {
            doubler(loop_var);
        }
        System.out.println("Hello, World!");
    }

    static void doubler(int n) {
        System.out.println(2*n);
    }
}
