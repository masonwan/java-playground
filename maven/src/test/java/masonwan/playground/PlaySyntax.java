package masonwan.playground;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PlaySyntax {
    @Test
    public void test_nullableToPrimitive() throws Exception {
        Throwable throwable = catchThrowable(() -> {
            @SuppressWarnings({ "unused", "ConstantConditions" })
            boolean isOkay = (Boolean) null;
        });

        assertThat(throwable)
            .isInstanceOf(NullPointerException.class);
    }
}
