package energy.usef.vudp.pbcfeeder;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import energy.usef.vudp.pbcfeeder.model.CategoryConfig;
import energy.usef.vudp.pbcfeeder.model.Connection;
import energy.usef.vudp.pbcfeeder.model.Data;
import energy.usef.vudp.pbcfeeder.model.Device;
import energy.usef.vudp.pbcfeeder.model.DeviceConfig;
import energy.usef.vudp.pbcfeeder.model.DeviceProfile;
import energy.usef.vudp.pbcfeeder.model.Settings;
import energy.usef.vudp.pbcfeeder.model.UncontrolledPower;

/**
 * Data Repository, responsible for retreiving the correct data from the excel.
 */
public class DataFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFactory.class);
    private static final String TAB_SETTINGS = "SETTINGS";
    private static final String TAB_CONNECTIONS = "CONNECTIONS";
    private static final String SEPERATOR = "_";

    private Data data;
    private List<CategoryConfig> categoryConfigs;
    private List<DeviceConfig> deviceConfigs;

    private List<UncontrolledPower> uncontrolledPower;
    private List<DeviceProfile> deviceProfiles;

    public DataFactory(Path pbcDataFile) {

        LOGGER.debug("Reading file : {} ", pbcDataFile);
        try {
            Workbook pbcWorkbook = WorkbookFactory.create(Files.newInputStream(pbcDataFile));

            data = new Data(readSettings(pbcWorkbook));

            categoryConfigs = CategoryConfig.readCategoryConfig(pbcWorkbook);

            deviceConfigs = DeviceConfig.readDeviceConfig(pbcWorkbook);

            uncontrolledPower = UncontrolledPower.readUncontrolledProfiles(pbcWorkbook);
            deviceProfiles = DeviceProfile.readDeviceProfiles(pbcWorkbook);

            initConnections(pbcWorkbook);

        } catch (IOException | InvalidFormatException e) {
            LOGGER.error("Path is not readable: {}", pbcDataFile, e);
        }

    }

    private void initConnections(Workbook pbcWorkbook) {
        Map<String, CategoryConfig> categoryConfigMap = categoryConfigs.stream()
                .collect(toMap(CategoryConfig::getName, Function.identity()));

        Map<String, List<UncontrolledPower>> uncontrolledPowerMap = uncontrolledPower.stream()
                .collect(groupingBy(UncontrolledPower::getProfile));

        for (Row row : pbcWorkbook.getSheet(TAB_CONNECTIONS)) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String ean = row.getCell(0).getStringCellValue();
            String category = row.getCell(1).getStringCellValue();

            if (categoryConfigMap.containsKey(category)) {
                CategoryConfig categoryConfig = categoryConfigMap.get(category);

                Connection connection = new Connection(ean, uncontrolledPowerMap.get(categoryConfig.getUncontrolled()));
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

            connection.getDevices().add(new Device(connection.getEan(),device, deviceConfig, deviceProfiles));
        }
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

