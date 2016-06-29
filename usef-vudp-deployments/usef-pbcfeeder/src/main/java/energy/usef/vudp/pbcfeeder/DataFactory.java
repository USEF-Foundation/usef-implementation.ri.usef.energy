package energy.usef.vudp.pbcfeeder;

import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Repository, responsible for retreiving the correct data from the excel.
 */
public class DataFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFactory.class);

    public static HashMap<String, Object> buildNewData(Path pbcDataFile) {
        LOGGER.debug("Reading file : {} ", pbcDataFile);
        if(pbcDataFile == null) {
//            LOGGER.error("No pbc data file configured under name: {}", AbstractConfig.CONFIG_FOLDER_PROPERTY, PBC_FILE_NAME);
        }

//        HSSFWorkbook pbcWorkbook = new HSSFWorkbook(pbcInput);
//        HSSFSheet pbcDataSheet = pbcWorkbook.getSheet(DATA_SHEET);
//        HSSFSheet pbcCongestionPointLimitsSheet = pbcWorkbook.getSheet(CPLIMITS_SHEET);
//        fillColStubInputMap(pbcDataSheet);
//        fillCongestionPointLimitsMaps(pbcCongestionPointLimitsSheet);
//        fillStubRowInputList(pbcDataSheet);

        return new HashMap<>();
    }


}
