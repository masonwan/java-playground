package masonwan.playground;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.testng.annotations.Test;

@Log4j2
public class PlayLog4j2 {
    @Test
    public void test_setRootLogLevel() throws Exception {
        Configurator.setRootLevel(Level.INFO);

        log.debug("Debug");
        log.info("Info");
        log.warn("Warn");
        log.error("Error");
    }
}
