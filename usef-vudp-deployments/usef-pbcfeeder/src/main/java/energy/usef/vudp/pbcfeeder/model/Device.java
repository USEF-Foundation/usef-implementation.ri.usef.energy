package energy.usef.vudp.pbcfeeder.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 */
public class Device {

    private String name;
    private String endpoint;
    private BigDecimal forecastDeviation;
    private Integer fluctuation;
    private Integer dtuSize;
    private String profile;

    private Map<Integer, Map<Integer, Power>> powerPerDayPerPtu;

    public Device(String ean, String name, DeviceConfig deviceConfig, List<DeviceProfile> deviceProfiles) {
        this.name = name;
        this.profile = deviceConfig.getCapabilityProfile();
        this.fluctuation = deviceConfig.getFluctuation();
        this.dtuSize = deviceConfig.getDtuSize();
        this.endpoint = "vudp://" + ean + "/" + name;

        forecastDeviation = BigDecimal.ZERO;
        if (deviceConfig.getForecastDeviation() > 0L) {
            Random r = new Random();
            forecastDeviation = new BigDecimal(1 + r.nextInt(deviceConfig.getForecastDeviation())).movePointLeft(2);
            if (r.nextBoolean()) {
                forecastDeviation = forecastDeviation.negate();
            }
        }

        powerPerDayPerPtu = deviceProfiles.stream().collect(Collectors.groupingBy(DeviceProfile::getDay,
                Collectors.toMap(DeviceProfile::getPtu, this::mapToPower)));
    }

    private Power mapToPower(DeviceProfile deviceProfile) {
        Power power = new Power(deviated(deviceProfile.getConsumption()),
                deviated(deviceProfile.getProduction()),
                deviated(deviceProfile.getFlexConsumption()),
                deviated(deviceProfile.getFlexProduction()));
        return power;
    }

    private Long deviated(Long power) {
        BigDecimal powerDecimal = new BigDecimal(power);
        BigDecimal deviation = forecastDeviation.multiply(powerDecimal);
        return powerDecimal.add(deviation).longValue();
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public BigDecimal getForecastDeviation() {
        return forecastDeviation;
    }

    public Integer getFluctuation() {
        return fluctuation;
    }

    public Integer getDtuSize() {
        return dtuSize;
    }

    public String getProfile() {
        return profile;
    }

    public Map<Integer, Map<Integer, Power>> getPowerPerDayPerPtu() {
        return powerPerDayPerPtu;
    }
}
