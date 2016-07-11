package energy.usef.vudp.pbcfeeder.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * An object to hold all relevant data together.
 */
public class Data {

    private Settings settings;

    private List<Connection> connection = new ArrayList<>();

    public Data() {
    }

    public Data(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<Connection> getConnection() {
        return connection;
    }

    public void setConnection(List<Connection> connection) {
        this.connection = connection;
    }
}
