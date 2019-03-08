package masonwan.playground;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayStream {
    @Test
    public void test_list() throws Exception {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);

        List<Integer> newList = list.stream()
            .map((num) -> num * 2)
            .collect(Collectors.toList());

        for (int i = 0; i < list.size(); i++) {
            assertThat(newList.get(i) == list.get(i) * 2);
        }
    }

    @Test
    public void test_toMap() throws Exception {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
        Map<Integer, Integer> map = list.stream()
            .map((num) -> num * 2)
            .collect(Collectors.toMap((num) -> num, (num) -> num * 2));

        assertThat(map.size()).isEqualTo(list.size());
        map.entrySet().forEach((num) -> {
            int key = num.getKey();
            int value = num.getValue();
            assertThat(value == key * 2).isTrue();
        });
    }

    @Test
    public void test_reduce() throws Exception {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
        Map<Integer, Integer> result = list.stream()
            .<Map<Integer, Integer>>reduce(
                new HashMap<>(),
                (map, a) -> {
                    map.put(a, a * 2);
                    return map;
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                }
            );

        assertThat(result.size()).isEqualTo(list.size());
        result.forEach((v1, v2) -> {
            assertThat(v1 * 2 == v2).isTrue();
        });
    }
}
