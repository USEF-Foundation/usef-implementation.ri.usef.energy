package energy.usef.vudp.pbcfeeder.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 */
public class UncontrolledProfile {

    public static final String TAB_UNCONTROLLED = "UNCONTROLLEDPROFILES";

    private String profile;
    private Integer day;
    private Integer ptu;
    private Long forecast;
    private Long observed;

    public UncontrolledProfile(String profile, Integer day, Integer ptu, Long forecast, Long observed) {
        this.profile = profile;
        this.day = day;
        this.ptu = ptu;
        this.forecast = forecast;
        this.observed = observed;
    }

    public static List<UncontrolledProfile> readUncontrolledProfiles(Workbook pbcWorkbook) {
        List<UncontrolledProfile> uncontrolledProfiles = new ArrayList<>();
        Sheet pbcDataSheet = pbcWorkbook.getSheet(TAB_UNCONTROLLED);
        for (Row row : pbcDataSheet) {
            if (row.getRowNum() == 0 || row.getCell(0) == null) {
                continue;
            }

            String profile = row.getCell(0).getStringCellValue();
            Integer day = (int) row.getCell(1).getNumericCellValue();
            Integer ptu = (int) row.getCell(2).getNumericCellValue();
            Long forecast = (long) row.getCell(3).getNumericCellValue();
            Long observed = (long) row.getCell(4).getNumericCellValue();

            UncontrolledProfile uncontrolledProfile = new UncontrolledProfile(profile, day, ptu, forecast, observed);
            uncontrolledProfiles.add(uncontrolledProfile);
        }
        return uncontrolledProfiles;
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

    public Long getForecast() {
        return forecast;
    }

    public Long getObserved() {
        return observed;
    }
}

