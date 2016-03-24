package masonwan.playground;

import java.util.HashMap;
import java.util.Map;

public class PlayHashCode {
    public static void main(String[] args) {
        Foo a1 = new Foo();
        Foo a2 = new Foo();
        Foo a3 = new Foo();
        Map<Foo, String> map = new HashMap<>();
        map.put(a1, "1 first");
        map.put(a2, "2 second");
        map.put(a3, "3 third");

        map.remove(a1);
        map.put(a3, "3 fourth");

        System.out.println(map.get(a2));
    }
}

class Foo {
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}