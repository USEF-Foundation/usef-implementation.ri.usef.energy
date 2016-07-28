package energy.usef.vudp.pbcfeeder.dto;

import java.util.Properties;

/**
 *
 */
public class Settings {

    private static final String KEY_RANDOM_SEED = "Random SEED";

    private Properties properties;

    public Settings(Properties properties) {
        this.properties = properties;
    }

    public long getSeed() {
        return Long.valueOf((String) properties.get(KEY_RANDOM_SEED));
    }
}
