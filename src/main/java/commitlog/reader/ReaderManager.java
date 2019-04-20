package commitlog.reader;

import commitlog.broker.BrokerService;
import commitlog.broker.TheBrokerService;
import commitlog.utils.Utils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

/**
 * Managers all the setup for the readers
 */
public class ReaderManager {
    private static final Logger log = Logger.getLogger(ReaderManager.class.getName());

    private BrokerService broker = TheBrokerService.getInstance();
    private Properties properties = new Properties();
    private HashMap<String, PartitionReaderConfig> configs = new HashMap<>();

    private HashMap<String, ExecutorService> executors = new HashMap<>();
    private Thread logReaderThread;

    /**
     *
     * @param args no args are required
     */
    public static void main( String[] args ) {
        ReaderManager manager = new ReaderManager();
        try {
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws Exception
     */
    private void start() throws Exception {
        //Initialize everything
        Utils.loadProperties(properties);
        broker.init(properties); //initialize the broker engine

        //Parse out partitions from the properties
        for (String key : properties.stringPropertyNames()) {
            log.info("Property: " + key + "=" + properties.getProperty(key));
            if (key.startsWith(Utils.PARTITION_DELIMITER)) {
                String[] keyParts = key.split("\\.");
                log.info("Parts:" + Arrays.toString(keyParts) + ":" + keyParts.length);
                if (keyParts.length == 4) {
                    int threadCount = Integer.parseInt(properties.getProperty(Utils.PARTITION_DELIMITER + "." + keyParts[1] + ".reader.threads"));
                    configs.put(keyParts[1], new PartitionReaderConfig(keyParts[1], threadCount));
                }
            }
        }

        //Partition readers
        log.debug("Starting executes for " + configs.size() + " partitions ");
        for (PartitionReaderConfig config : configs.values()) {
            log.info("creating " + config.getThreads() + " threads for " + config.getId() );
            executors.put( config.getId(),  Executors.newFixedThreadPool(config.getThreads()) );
        }

        //
        startReadingLog();
    }

    /**
     *
     */
    private void startReadingLog() throws InterruptedException {
        logReaderThread = new Thread( new Reader( broker, executors ), "logReaderThread" );
        logReaderThread.start();
    }
}
