package masonwan.playground;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.testng.annotations.Test;

import java.util.List;

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

    @Test
    public void test_ignoreUnknownProperties() throws Exception {
        String json = "{\"adult\":false,\"backdrop_path\":\"/9ibkfEhMROCE2gkPTozG6xVsa0G.jpg\",\"belongs_to_collection\":{\"id\":304378,\"name\":\"Independence Day Collection\",\"poster_path\":\"/7qJJRGcZv8UZUdjOXV9P3jNElPq.jpg\",\"backdrop_path\":\"/p7oqa94XgNGVMazXwR49QfyGgtx.jpg\"},\"budget\":200000000,\"genres\":[{\"id\":28,\"name\":\"Action\"},{\"id\":12,\"name\":\"Adventure\"},{\"id\":878,\"name\":\"Science Fiction\"}],\"homepage\":\"http://www.warof1996.com\",\"id\":47933,\"imdb_id\":\"tt1628841\",\"original_language\":\"en\",\"original_title\":\"Independence Day: Resurgence\",\"overview\":\"We always knew they were coming back. Using recovered alien technology, the nations of Earth have collaborated on an immense defense program to protect the planet. But nothing can prepare us for the aliens’ advanced and unprecedented force. Only the ingenuity of a few brave men and women can bring our world back from the brink of extinction.\",\"popularity\":1.187036,\"poster_path\":\"/9KQX22BeFzuNM66pBA6JbiaJ7Mi.jpg\",\"production_companies\":[{\"name\":\"Twentieth Century Fox Film Corporation\",\"id\":306}],\"production_countries\":[{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2016-06-22\",\"revenue\":0,\"runtime\":0,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"}],\"status\":\"Post Production\",\"tagline\":\"We had twenty years to prepare. So did they.\",\"title\":\"Independence Day: Resurgence\",\"video\":false,\"vote_average\":5.5,\"vote_count\":27}";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Movie movie = objectMapper.readValue(json, Movie.class);

        assertThat(movie.getGenres())
            .hasSize(3);
    }

    @Builder
    @Data
    @AllArgsConstructor
    private static class Movie {
        Integer id;
        List<Genre> genres;
    }

    @Data
    private static class Genre {
        Integer id;
        String name;
    }
}
