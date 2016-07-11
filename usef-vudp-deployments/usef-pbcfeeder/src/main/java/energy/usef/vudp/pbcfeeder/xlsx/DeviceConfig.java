package energy.usef.vudp.pbcfeeder.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * An object representing the configuration for a specific device.
 */
public class DeviceConfig {

    public static final String TAB_DEVICES = "DEVICES";

    private String name;
    private String profile;
    private String capabilityProfile;
    private Integer dtuSize;
    private Integer forecastDeviation;//set deviation per device
    private Integer observedDeviation;//set deviation of evential outcome
    private Integer fluctuation;// fluctuation per day

    private DeviceConfig(String name, String profile, String capabilityProfile, Integer dtuSize, Integer forecastDeviation,
            Integer observedDeviation, Integer fluctuation) {
        this.name = name;
        this.profile = profile;
        this.capabilityProfile = capabilityProfile;
        this.dtuSize = dtuSize;
        this.forecastDeviation = forecastDeviation;
        this.observedDeviation = observedDeviation;
        this.fluctuation = fluctuation;
    }

    public static List<DeviceConfig> readDeviceConfig(Workbook pbcWorkbook) {
        List<DeviceConfig> deviceConfigs = new ArrayList<>();
        Sheet pbcDataSheet = pbcWorkbook.getSheet(TAB_DEVICES);
        for (Row row : pbcDataSheet) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String name = row.getCell(0).getStringCellValue();
            Integer dtuSize = (int) row.getCell(1).getNumericCellValue();
            String profile = row.getCell(2).getStringCellValue();
            Integer forecastDeviation = (int) row.getCell(3).getNumericCellValue();
            Integer observedDeviation = (int) row.getCell(4).getNumericCellValue();
            Integer fluctuation = (int) row.getCell(5).getNumericCellValue();
            String capabilityProfile = row.getCell(6).getStringCellValue();

            DeviceConfig deviceConfig = new DeviceConfig(name, profile, capabilityProfile, dtuSize, forecastDeviation,
                    observedDeviation, fluctuation);
            deviceConfigs.add(deviceConfig);
        }
        return deviceConfigs;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public String getCapabilityProfile() {
        return capabilityProfile;
    }

    public Integer getDtuSize() {
        return dtuSize;
    }

    public Integer getForecastDeviation() {
        return forecastDeviation;
    }

    public Integer getObservedDeviation() {
        return observedDeviation;
    }

    public Integer getFluctuation() {
        return fluctuation;
    }
}
