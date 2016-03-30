package masonwan.playground;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayJackson {
    @Test
    public void testTree_findValue() throws Exception {
        String json = "{\"x\":{\"y\":{\"z\":123}}}";
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(json);
        int value = jsonNode.findValue("z").intValue();

        assertThat(value).isEqualTo(123);
    }
    @Test
    public void testTree_findPath() throws Exception {
        String json = "{\"x\":{\"y\":{\"z\":123}}}";
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(json);
        int value = jsonNode.findPath("should not find me").asInt(321);

        assertThat(value).isEqualTo(321);
    }
}
