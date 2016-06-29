package energy.usef.vudp.pbcfeeder;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import energy.usef.core.config.AbstractConfig;
import energy.usef.vudp.pbcfeeder.watcher.FileWatcher;
import energy.usef.vudp.pbcfeeder.watcher.Updatable;

/**
 * Data Repository, responsible for retreiving the correct data from the excel.
 */
@ApplicationScoped
public class DataRepository implements Updatable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRepository.class);

    private static final String PBC_FILE_NAME = "vudp-pbc-data.xlsx";

    private ExecutorService watcherExecutor = Executors.newSingleThreadExecutor();

    private Path pbcDataFile;

    private HashMap<String, Object> currentData = new HashMap<>();

    @PostConstruct
    public void init() {
        pbcDataFile = new File(System.getProperty(AbstractConfig.CONFIG_FOLDER_PROPERTY)).toPath().resolve(PBC_FILE_NAME);
        update();
        watcherExecutor.execute(new FileWatcher(pbcDataFile, this));
    }

    public void update() {
        if(pbcDataFile == null) {
            LOGGER.error("No pbc data file configured under name: {}/{}", AbstractConfig.CONFIG_FOLDER_PROPERTY, PBC_FILE_NAME);
        }
        currentData = DataFactory.buildNewData(pbcDataFile);
    }

}
