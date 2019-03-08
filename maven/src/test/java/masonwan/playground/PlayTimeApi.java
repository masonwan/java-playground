package masonwan.playground;

import org.testng.annotations.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayTimeApi {

    @Test
    public void testInstant_toLocalDate() {
        String dateString = "1980-02-29";
        Instant time = Instant.parse(dateString + "T00:00:00.000Z");
        LocalDate date = time.atZone(ZoneOffset.UTC).toLocalDate();

        assertThat(date.toString()).isEqualTo(dateString);
    }
}
