package energy.usef.vudp.pbcfeeder.dto;

import java.util.Properties;

/**
 *
 */
public class Settings {

    private Properties properties;

    public Settings(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
