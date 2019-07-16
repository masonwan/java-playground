package masonwan.playground;

import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayTime {
    @Test
    public void instant_parse() {
        Instant instant = Instant.parse("1980-02-29T23:59:59.999Z");

        assertThat(instant.get(ChronoField.MILLI_OF_SECOND)).isEqualTo(999);
    }
}
