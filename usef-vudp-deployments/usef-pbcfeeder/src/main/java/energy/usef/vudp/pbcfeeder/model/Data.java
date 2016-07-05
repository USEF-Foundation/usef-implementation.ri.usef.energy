package energy.usef.vudp.pbcfeeder.model;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private Settings settings;

    private List<Connection> connection = new ArrayList<>();

    public Data(Settings settings) {
        this.settings = settings;
    }

    public List<Connection> getConnection() {
        return connection;
    }
}
