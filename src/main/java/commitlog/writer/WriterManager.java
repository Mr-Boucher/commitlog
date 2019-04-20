package commitlog.writer;


import commitlog.broker.BrokerService;
import commitlog.broker.TheBrokerService;
import commitlog.utils.Utils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Manages threads for writing
 */
public class WriterManager {

    private static final Logger log = Logger.getLogger(WriterManager.class.getName());

    private BrokerService broker = TheBrokerService.getInstance();
    private Properties properties = new Properties();
    private HashMap<String, PartitionWriterConfig> configs = new HashMap<>();

    private List<Thread> writers = new ArrayList<>();

    /**
     *
     * @param args no args are required
     */
    public static void main( String[] args ) {
        WriterManager manager = new WriterManager();
        try {
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void start() throws Exception {

        //Initialize everything
        Utils.loadProperties(properties);
        broker.init(properties); //initialize the broker engine

        //Parse out configs from the properties
        for (String key : properties.stringPropertyNames()) {
            log.info("Property: " + key + "=" + properties.getProperty(key));
            if (key.startsWith(Utils.PARTITION_DELIMITER)) {
                String[] keyParts = key.split("\\.");
                log.info("Parts:" + Arrays.toString(keyParts) + ":" + keyParts.length);
                if (keyParts.length >= 4) {
                    int threadCount = Integer.parseInt(properties.getProperty(Utils.PARTITION_DELIMITER + "." + keyParts[1] + ".writer.threads"));
                    int iterations = Integer.parseInt(properties.getProperty(Utils.PARTITION_DELIMITER + "." + keyParts[1] + ".writer.iterations"));
                    String type = properties.getProperty( Utils.PARTITION_DELIMITER + "." + keyParts[1] + ".writer.type", null );

                    configs.put(keyParts[1], new PartitionWriterConfig(keyParts[1], iterations, threadCount, type));
                }
            }
        }

        //Start all the threads
        log.debug("Starting threads for " + configs.size() + " configs ");
        for (PartitionWriterConfig partitionWriterConfig : configs.values()) {
            log.info(partitionWriterConfig);
            startThreads(partitionWriterConfig);
        }
    }

    /**
     * @param partitionWriterConfig
     * @throws InterruptedException
     */
    private void startThreads(PartitionWriterConfig partitionWriterConfig) throws Exception {

        //Create and start N number of threads per CID/partition
        for (int i = 0; i < partitionWriterConfig.getThreads(); i++) {
            String name = partitionWriterConfig.getId() + "(" + i + ")";
            log.info("Starting writer" + name);
            Thread thread = new Thread(new Writer(properties, name, broker, partitionWriterConfig), name);
            writers.add( thread );
            thread.start();
        }
    }
}
