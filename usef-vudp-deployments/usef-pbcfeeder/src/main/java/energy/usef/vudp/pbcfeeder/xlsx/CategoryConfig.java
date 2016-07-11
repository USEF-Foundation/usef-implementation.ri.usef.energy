package energy.usef.vudp.pbcfeeder.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Object representing the configuration of one Category.
 */
public class CategoryConfig {

    public static final String TAB_CATEGORIES = "CATEGORIES";

    private String name;
    private String uncontrolled;
    private List<String> devices;

    private CategoryConfig(String name, String uncontrolled, List<String> devices) {
        this.name = name;
        this.uncontrolled = uncontrolled;
        this.devices = devices;
    }

    public static List<CategoryConfig> readCategoryConfig(Workbook pbcWorkbook) {
        List<CategoryConfig> categoryConfigs = new ArrayList<>();
        Sheet pbcDataSheet = pbcWorkbook.getSheet(TAB_CATEGORIES);
        for (Row row : pbcDataSheet) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String name = row.getCell(0).getStringCellValue();
            String uncontrolled = row.getCell(1).getStringCellValue();
            List<String> devices = new ArrayList<>();
            if (row.getCell(2) != null) {
                String[] splitDevices = row.getCell(2).getStringCellValue().split(",");
                for (String device : splitDevices) {
                    if (StringUtils.isNotEmpty(device)) {
                        devices.add(device.trim());
                    }
                }
            }

            CategoryConfig deviceConfig = new CategoryConfig(name, uncontrolled, devices);
            categoryConfigs.add(deviceConfig);
        }
        return categoryConfigs;
    }

    public String getName() {
        return name;
    }

    public String getUncontrolled() {
        return uncontrolled;
    }

    public List<String> getDevices() {
        return devices;
    }
}
