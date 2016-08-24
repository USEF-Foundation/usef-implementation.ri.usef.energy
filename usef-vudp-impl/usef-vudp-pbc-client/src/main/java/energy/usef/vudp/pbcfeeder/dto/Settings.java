package energy.usef.vudp.pbcfeeder.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class Settings {

    private static final String KEY_RANDOM_SEED = "Random SEED";
    private static final String KEY_DAY_CYCLE = "Day Cycle";

    private Properties properties;

    public Settings(Properties properties) {
        this.properties = properties;
    }

    public long getSeed() {
        return Long.valueOf((String) properties.get(KEY_RANDOM_SEED));
    }

    public List<String> getDayCycle() {
        String dayCycleProperty = (String) properties.get(KEY_DAY_CYCLE);
        return Arrays.asList(dayCycleProperty.split("\\s*,\\s*"));
    }
}
