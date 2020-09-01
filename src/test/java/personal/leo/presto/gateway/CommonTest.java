package personal.leo.presto.gateway;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommonTest {
    @Test
    public void test() {
        System.out.println(Arrays.asList("a", "b").stream()
                .filter(s -> s.equalsIgnoreCase("c"))
                .collect(Collectors.toList()));
    }
}
