package energy.usef.vudp.pbcfeeder.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class Connection {
    private String ean;

    private Map<Integer, Map<Integer, UncontrolledPower>> uncontrolled;

    private List<Device> devices = new ArrayList<>();

    public Connection(String ean, List<UncontrolledPower> uncontrolled) {
        this.ean = ean;
        this.uncontrolled = uncontrolled.stream().collect(Collectors.groupingBy(UncontrolledPower::getDay,
                Collectors.toMap(UncontrolledPower::getPtu, Function.identity())));
    }

    public String getEan() {
        return ean;
    }

    public Map<Integer, Map<Integer, UncontrolledPower>> getUncontrolled() {
        return uncontrolled;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
