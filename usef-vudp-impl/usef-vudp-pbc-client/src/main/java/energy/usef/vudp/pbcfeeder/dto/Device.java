package energy.usef.vudp.pbcfeeder.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Object representing a device.
 */
public class Device {

    private String name;
    private String endpoint;
    private BigDecimal forecastDeviation;
    private Integer fluctuation;
    private Integer dtuSize;
    private String profile;

    private Map<Integer, Map<Integer, Power>> powerPerDayPerPtu;

    public Device() {
    }

    public Device(String name, String endpoint, BigDecimal forecastDeviation, Integer fluctuation, Integer dtuSize,
            String profile,
            Map<Integer, Map<Integer, Power>> powerPerDayPerPtu) {
        this.name = name;
        this.endpoint = endpoint;
        this.forecastDeviation = forecastDeviation;
        this.fluctuation = fluctuation;
        this.dtuSize = dtuSize;
        this.profile = profile;
        this.powerPerDayPerPtu = powerPerDayPerPtu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public BigDecimal getForecastDeviation() {
        return forecastDeviation;
    }

    public void setForecastDeviation(BigDecimal forecastDeviation) {
        this.forecastDeviation = forecastDeviation;
    }

    public Integer getFluctuation() {
        return fluctuation;
    }

    public void setFluctuation(Integer fluctuation) {
        this.fluctuation = fluctuation;
    }

    public Integer getDtuSize() {
        return dtuSize;
    }

    public void setDtuSize(Integer dtuSize) {
        this.dtuSize = dtuSize;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Map<Integer, Map<Integer, Power>> getPowerPerDayPerPtu() {
        return powerPerDayPerPtu;
    }

    public void setPowerPerDayPerPtu(
            Map<Integer, Map<Integer, Power>> powerPerDayPerPtu) {
        this.powerPerDayPerPtu = powerPerDayPerPtu;
    }
}
