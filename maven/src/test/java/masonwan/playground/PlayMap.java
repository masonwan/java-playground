package masonwan.playground;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayMap {
    @Test
    public void test_sameHashCode() throws Exception {
        TestedClass a1 = new TestedClass();
        TestedClass a2 = new TestedClass();
        TestedClass a3 = new TestedClass();
        Map<TestedClass, String> map = new HashMap<>();
        map.put(a1, "1 first");
        map.put(a2, "2 second");
        map.put(a3, "3 third");

        map.remove(a1);
        map.put(a3, "3 fourth");

        assertThat(map.get(a3))
            .isEqualTo("3 fourth");
    }

    static class TestedClass {
        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}