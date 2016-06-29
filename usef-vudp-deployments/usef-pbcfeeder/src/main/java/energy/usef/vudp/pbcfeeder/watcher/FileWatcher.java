package energy.usef.vudp.pbcfeeder.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to watch a file
 */
public class FileWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    private Path fileToWatch;
    private Updatable updatable;

    public FileWatcher(Path fileToWatch, Updatable updatable) {
        this.fileToWatch = fileToWatch;
        this.updatable = updatable;
    }

    @Override
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path directoryToWatch = fileToWatch.getParent();
            directoryToWatch.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                handleOneEvent(watcher.take(), fileToWatch);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Exception caught in the FileWatcher of the PBC Feeder Excel sheet.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleOneEvent(WatchKey key, Path fileToWatch) {

        for (WatchEvent<?> event : key.pollEvents()) {
            if (event == null || event.kind() == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }
            LOGGER.debug("Modification of the file occurred: {}", event.context());

            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

            try {
                String filename = fileToWatch.getFileName().toString();
                String changedFile = fileToWatch.getParent().resolve(pathEvent.context()).toRealPath().getFileName().toString();
                if (filename.equalsIgnoreCase(changedFile)) {
                    LOGGER.info("Modification of file occurred: {}, update requested.", changedFile);
                    updatable.update();
                }
            } catch (IOException e) {
                // This exception has no functional impact. It may happen due to the saving process of MS Excel.
                LOGGER.debug("Exception caught while listening to the modification event of the PBC Feeder Excel sheet.", e);
            }
        }
        key.reset();
    }
}
