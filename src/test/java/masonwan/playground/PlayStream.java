package masonwan.playground;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class PlayStream {
    @Test
    public void testToList() throws Exception {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);

        List<Integer> newList = list.stream()
            .map((num) -> num * 2)
            .collect(Collectors.toList());

        for (int i = 0; i < list.size(); i++) {
            assertThat(newList.get(i) == list.get(i) * 2);
        }
    }

    @Test
    public void testToMap() throws Exception {
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
}
