package energy.usef.vudp.pbcfeeder;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import energy.usef.vudp.pbcfeeder.dto.Connection;
import energy.usef.vudp.pbcfeeder.dto.Data;
import energy.usef.vudp.pbcfeeder.dto.Device;
import energy.usef.vudp.pbcfeeder.dto.Power;
import energy.usef.vudp.pbcfeeder.dto.Settings;
import energy.usef.vudp.pbcfeeder.dto.UncontrolledPower;
import energy.usef.vudp.pbcfeeder.xlsx.CategoryConfig;
import energy.usef.vudp.pbcfeeder.xlsx.DeviceConfig;
import energy.usef.vudp.pbcfeeder.xlsx.DeviceProfile;
import energy.usef.vudp.pbcfeeder.xlsx.UncontrolledProfile;

/**
 * Data Repository, responsible for retreiving the correct data from the excel.
 */
public class DataFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFactory.class);
    private static final String TAB_SETTINGS = "SETTINGS";
    private static final String TAB_CONNECTIONS = "CONNECTIONS";
    private static final String SEPERATOR = "_";

    private Random RANDOM;
    private List<String> dayCycle;

    private Data data;
    private List<CategoryConfig> categoryConfigs;
    private List<DeviceConfig> deviceConfigs;

    private List<UncontrolledProfile> uncontrolledProfile;
    private List<DeviceProfile> deviceProfiles;

    public DataFactory(Path pbcDataFile) {

        LOGGER.debug("Reading file : {} ", pbcDataFile);
        try {
            Workbook pbcWorkbook = WorkbookFactory.create(Files.newInputStream(pbcDataFile));

            data = new Data(readSettings(pbcWorkbook));

            RANDOM = new Random(data.getSettings().getSeed());

            dayCycle = data.getSettings().getDayCycle();

            categoryConfigs = CategoryConfig.readCategoryConfig(pbcWorkbook);

            deviceConfigs = DeviceConfig.readDeviceConfig(pbcWorkbook);

            uncontrolledProfile = UncontrolledProfile.readUncontrolledProfiles(pbcWorkbook);
            deviceProfiles = DeviceProfile.readDeviceProfiles(pbcWorkbook);

            initConnections(pbcWorkbook);

        } catch (IOException | InvalidFormatException e) {
            LOGGER.error("Path is not readable: {}", pbcDataFile, e);
        }

    }

    public Random getSeededRandom() {
        return RANDOM;
    }

    public List<String> getDayCycle() {
        return dayCycle;
    }

    private void initConnections(Workbook pbcWorkbook) {
        Map<String, CategoryConfig> categoryConfigMap = categoryConfigs.stream()
                .collect(toMap(CategoryConfig::getName, Function.identity()));

        Map<String, List<UncontrolledProfile>> uncontrolledProfilesMap = uncontrolledProfile.stream()
                .collect(groupingBy(UncontrolledProfile::getProfile));

        for (Row row : pbcWorkbook.getSheet(TAB_CONNECTIONS)) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String ean = row.getCell(0).getStringCellValue();
            String category = row.getCell(1).getStringCellValue();

            if (categoryConfigMap.containsKey(category)) {
                CategoryConfig categoryConfig = categoryConfigMap.get(category);

                List<UncontrolledProfile> uncontrolledProfiles = uncontrolledProfilesMap.get(categoryConfig.getUncontrolled());
                Map<Integer, Map<Integer, UncontrolledPower>> uncontrolledPowerMap = uncontrolledProfiles.stream()
                        .collect(groupingBy(UncontrolledProfile::getDay,
                                Collectors.toMap(UncontrolledProfile::getPtu,
                                        up -> new UncontrolledPower(up.getForecast(), up.getObserved()))));

                Connection connection = new Connection(ean, uncontrolledPowerMap);
                initDevices(connection, categoryConfig.getDevices());

                data.getConnection().add(connection);
            }

        }
    }

    private void initDevices(Connection connection, List<String> devices) {
        Map<String, DeviceConfig> deviceConfigMap = deviceConfigs.stream()
                .collect(Collectors.toMap(DeviceConfig::getName, Function.identity()));

        Map<String, List<DeviceProfile>> deviceProfileMap = deviceProfiles.stream()
                .collect(groupingBy(DeviceProfile::getProfile));

        for (String device : devices) {
            String deviceType = device.split(SEPERATOR)[0];
            DeviceConfig deviceConfig = deviceConfigMap.get(deviceType);
            List<DeviceProfile> deviceProfiles = deviceProfileMap.get(deviceConfig.getProfile());

            connection.getDevices().add(createDevice(connection.getEan(), device, deviceConfig, deviceProfiles));
        }
    }

    private Device createDevice(String ean, String name, DeviceConfig deviceConfig, List<DeviceProfile> deviceProfiles) {
        String endpoint = "vudp://" + ean + "/" + name;

        BigDecimal forecastDeviation = BigDecimal.ZERO;
        if (deviceConfig.getForecastDeviation() > 0L) {
            forecastDeviation = new BigDecimal(1 + RANDOM.nextInt(deviceConfig.getForecastDeviation())).movePointLeft(2);
            if (RANDOM.nextBoolean()) {
                forecastDeviation = forecastDeviation.negate();
            }
        }

        final BigDecimal finalForecastDeviation = forecastDeviation;
        Map<Integer, Map<Integer, Power>> powerPerDayPerPtu = deviceProfiles.stream()
                .collect(groupingBy(DeviceProfile::getDay,
                        Collectors
                                .toMap(DeviceProfile::getPtu, deviceProfile -> mapToPower(deviceProfile, finalForecastDeviation))));

        return new Device(name, endpoint, forecastDeviation, deviceConfig.getFluctuation(), deviceConfig.getDtuSize(),
                deviceConfig.getProfile(),deviceConfig.getCapabilityProfile(), powerPerDayPerPtu);
    }

    private Power mapToPower(DeviceProfile deviceProfile, BigDecimal finalForecastDeviation) {
        Power power = new Power(deviated(deviceProfile.getConsumption(), finalForecastDeviation),
                deviated(deviceProfile.getProduction(), finalForecastDeviation),
                deviated(deviceProfile.getFlexConsumption(), finalForecastDeviation),
                deviated(deviceProfile.getFlexProduction(), finalForecastDeviation));
        return power;
    }

    private Long deviated(Long power, BigDecimal finalForecastDeviation) {
        BigDecimal powerDecimal = new BigDecimal(power);
        BigDecimal deviation = finalForecastDeviation.multiply(powerDecimal);
        return powerDecimal.add(deviation).longValue();
    }

    private Settings readSettings(Workbook pbcWorkbook) {
        Sheet pbcDataSheet = pbcWorkbook.getSheet(TAB_SETTINGS);
        Properties settings = new Properties();
        for (Row row : pbcDataSheet) {
            String key = row.getCell(0).getStringCellValue();
            if (StringUtils.isNotEmpty(key) && row.getCell(1) != null) {
                String value = row.getCell(1).toString();
                settings.setProperty(key, value);
            }
        }
        return new Settings(settings);
    }

    public Data getData() {
        return data;
    }
}

