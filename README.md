# ClassReader
A wrapper around .class files, parses to and writes class files from objects.

## Examples
See https://github.com/pranavgade20/Java-Disassembler for more details.

```java
import io.github.pranavgade20.classexplorer.*;
import java.io.*;
public class Test {
    static volatile int MAGIC = 42;
    public final String x = "42";
    public static void main(String[] args) throws IOException {
        Klass klass = new Klass(new FileInputStream("Test.class"));
        
        System.out.println(klass.access_flags.toString() + " class " + klass.this_class.name + " extends " + klass.super_class.name);
        System.out.println();
        
        System.out.println("Fields:");
        for (FieldInfo field : klass.fields) System.out.println(field.access_flags.toString() + " " + field.name + " " + field.descriptor);
        System.out.println();
        
        System.out.println("Methods:");
        for (MethodInfo method : klass.methods) System.out.println(method.access_flags.toString() + " " + method.name + " " + method.descriptor);
    }

    private long myMethod(int num) {
        return 42L * num;
    }
}
```

### Output
```
Public class Test extends java/lang/Object

Fields:
Static,Volatile,Super MAGIC I
Public,Final x Ljava/lang/String;

Methods:
Public <init> ()V
Public,Static main ([Ljava/lang/String;)V
Private myMethod ()J
Static <clinit> ()V
```
