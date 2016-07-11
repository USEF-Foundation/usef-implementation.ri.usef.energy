package energy.usef.vudp.pbcfeeder.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Object representing a connection.
 */
public class Connection {
    private String ean;

    private Map<Integer, Map<Integer, UncontrolledPower>> uncontrolled;

    private List<Device> devices = new ArrayList<>();

    public Connection() {
    }

    public Connection(String ean, Map<Integer, Map<Integer, UncontrolledPower>> uncontrolled) {
        this.ean = ean;
        this.uncontrolled = uncontrolled;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Map<Integer, Map<Integer, UncontrolledPower>> getUncontrolled() {
        return uncontrolled;
    }

    public void setUncontrolled(
            Map<Integer, Map<Integer, UncontrolledPower>> uncontrolled) {
        this.uncontrolled = uncontrolled;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
