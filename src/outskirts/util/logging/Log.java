package outskirts.util.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Log {

    static {
        try {
            // load configuration before get related logger.
            Configurator.initialize(null, new ConfigurationSource(new FileInputStream(new File("log4j2.xml"))));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public final static Logger LOGGER = LogManager.getLogger();

    public static void info(Object msg, Object... args) {
        LOGGER.info(String.valueOf(msg), args);
    }

    public static void warn(Object msg, Object... args) {
        LOGGER.warn(String.valueOf(msg), args);
    }


}
