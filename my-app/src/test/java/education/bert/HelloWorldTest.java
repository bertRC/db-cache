package education.bert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelloWorldTest {
    @Test
    public void helloTest() {
        HelloWorld helloWorld = new HelloWorld();
        String text = helloWorld.printHello();
        assertNotNull(text);
    }
}

