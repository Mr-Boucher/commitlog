package commitlog.writer;

import commitlog.broker.BrokerService;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 *
 */
public class Writer implements Runnable {
    private static final Logger log = Logger.getLogger(Writer.class.getName());

    private BrokerService broker;
    private PartitionWriterConfig partitionWriterConfig;
    private String writerId;
    private Properties properties;

    public Writer(Properties properties, String writerId, BrokerService broker, PartitionWriterConfig partitionWriterConfig) {
        this.properties = properties;
        this.writerId = writerId;
        this.broker = broker;
        this.partitionWriterConfig = partitionWriterConfig;
    }

    @Override
    public void run() {
        try {
            //
            String randStr = properties.getProperty( "partition.random_sleep" );
            int rand = 2000; //default of 2 second max random sleep time
            try {
                if (randStr != null)
                    rand = Integer.parseInt(randStr);
            }
            catch (NumberFormatException npe )
            {
                log.error( randStr + " is an invalid int for partitionWriterConfig.random_sleep");
            }

            //write out the number of iterations
            for(int i = 0; i < partitionWriterConfig.getIterations(); i++ ) {

                //if the number is number then generate a random number
                if( "number".equals( partitionWriterConfig.getType() ) )
                    broker.write(partitionWriterConfig.getId(), Long.toString((long) (Math.random() * rand)));
                else
                    broker.write(partitionWriterConfig.getId(), "This is data written by Writer " + writerId + " on partitionWriterConfig " + partitionWriterConfig.getId() + " for iteration " + i);
                Thread.sleep((long) (Math.random() * rand ));
            }
        } catch (Exception e) {
            log.error("Writer " + writerId + "died because of:", e);
        }
    }
}
