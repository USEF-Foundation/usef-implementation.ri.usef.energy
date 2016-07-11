package energy.usef.vudp.pbcfeeder.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * An object representing the base profile for a device type/profile.
 */
public class DeviceProfile {

    public static final String TAB_DEVICE_PROFILES = "DEVICEPROFILES";

    private String profile;
    private Integer day;
    private Integer ptu;
    private Long consumption;
    private Long production;
    private Long flexConsumption;
    private Long flexProduction;

    private DeviceProfile(String profile, Integer day, Integer ptu, Long consumption, Long production, Long flexConsumption,
            Long flexProduction) {
        this.profile = profile;
        this.day = day;
        this.ptu = ptu;
        this.consumption = consumption;
        this.production = production;
        this.flexConsumption = flexConsumption;
        this.flexProduction = flexProduction;
    }

    public static List<DeviceProfile> readDeviceProfiles(Workbook pbcWorkbook) {
        List<DeviceProfile> deviceProfiles = new ArrayList<>();
        Sheet pbcDataSheet = pbcWorkbook.getSheet(TAB_DEVICE_PROFILES);
        for (Row row : pbcDataSheet) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String profile = row.getCell(0).getStringCellValue();
            Integer day = (int) row.getCell(1).getNumericCellValue();
            Integer ptu = (int) row.getCell(2).getNumericCellValue();
            Long consumption = (long) row.getCell(3).getNumericCellValue();
            Long production = (long) row.getCell(4).getNumericCellValue();
            Long flexConsumption = (long) row.getCell(5).getNumericCellValue();
            Long flexProduction = (long) row.getCell(6).getNumericCellValue();

            DeviceProfile deviceProfile = new DeviceProfile(profile, day, ptu, consumption, production, flexConsumption,
                    flexProduction);
            deviceProfiles.add(deviceProfile);
        }
        return deviceProfiles;
    }

    public String getProfile() {
        return profile;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getPtu() {
        return ptu;
    }

    public Long getConsumption() {
        return consumption;
    }

    public Long getProduction() {
        return production;
    }

    public Long getFlexConsumption() {
        return flexConsumption;
    }

    public Long getFlexProduction() {
        return flexProduction;
    }
}
