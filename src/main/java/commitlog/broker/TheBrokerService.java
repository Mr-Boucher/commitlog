package commitlog.broker;

import commitlog.model.Entry;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Managers the commit log
 */
public class TheBrokerService implements BrokerService {

    private static final Logger log = Logger.getLogger(TheBrokerService.class.getName());
    private static final Object syncObject = new Object();

    private static final TheBrokerService instance = new TheBrokerService();
    private Properties properties;

    private File file;
    private Writer output;
    private BufferedReader input;

    /**
     * @return
     */
    public static TheBrokerService getInstance() {
        return instance;
    }

    private TheBrokerService() {

    }

    /**
     *
     */
    public void init(Properties properties) throws IOException {
        this.properties = properties;
        String fileName = properties.getProperty("commit_log.file_name");
        if (fileName == null || fileName.trim().length() == 0)
            throw new IllegalArgumentException("Missing commit_log.file_name property.");

        //make sure the file is setup correctly
        this.file = new File(fileName);
        if (!this.file.exists())
            log.info("Creating file " + file.getAbsolutePath());

        log.info("Initializing broker with commit log file: " + fileName + " (" + file.getAbsolutePath() + ")");

        //
        String clearOnStart = properties.getProperty("commit_log.clear_on_start", "false");
        boolean clearFile = clearOnStart == null || Boolean.parseBoolean(clearOnStart);
        if (clearFile)
            log.info("Truncating file " + file.getAbsolutePath());
        else
            log.info("Appending to file " + file.getAbsolutePath());

        //open file for write and read
        this.output = new BufferedWriter(new FileWriter(fileName, !clearFile));
        this.input = new BufferedReader(new FileReader(fileName));
    }

    /**
     * clean up resources
     */
    public void shutdown() {
        try {
            this.output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.input.close();
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * @param callerId a unique id for the caller.
     * @param data     String of data
     * @return
     */
    public Entry write(String callerId, String data) throws IOException {

        if (output == null)
            throw new RuntimeException("Invalid Broker not initialized");

        Entry entry = new Entry(callerId, data);
        if (log.isDebugEnabled()) log.debug("Inserting entry: " + entry);
        appendToLog(entry);
        if (log.isDebugEnabled()) log.debug("Inserted entry: " + entry);
        return entry;
    }


    /**
     * Returns null if no data, unless blocking is true this it will wait forever
     * or until there is a new entry for the partition
     *
     * @param partition the partition to read the next line for, if null reads any partition
     * @param block wait for next entry for this partition
     *
     * @return null or Entry
     * @throws IOException
     */
    public Entry read(String partition, boolean block) throws IOException {
        if (output == null)
            throw new RuntimeException("Invalid Broker not initialized");

        log.trace("Reading partition: " + partition);

        //
        File file = new File(properties.getProperty("commit_log.file_name"));
        log.trace("Reading file " + file.getAbsolutePath());

        //Read one entry from file if blocking is true it will
        String line;
        String data = null;
        do {
            while ((line = input.readLine()) != null) {
                if (partition == null || line.startsWith(partition))
                    data = line;
            }
            if (log.isTraceEnabled()) log.trace("Read entry: " + partition + " found " + data );
            if( block && data == null ) {
                if( log.isTraceEnabled() ) log.trace( "Blocking" );
                try {
                    Thread.sleep( 250 );
                } catch (InterruptedException e) {
                    if( log.isDebugEnabled( ) ) log.debug( "Sleep interrupted", e );
                }
            }

        } while( block && data == null);

        //Parse the entry for the string result
        Entry entry = null;
        if( data != null ) {
            String[] result = data.split(":");
            if( result.length != 3 )
                log.error( "invalid entry returned: " + data );
            else {
                long entryId = Long.parseLong(result[1].trim());
                entry = new Entry(entryId, result[0].trim(), result[2].trim());
            }
        }
        return entry;
    }

    /**
     * @param entry
     */
    private void appendToLog(Entry entry) throws IOException {
        if (entry == null)
            throw new RuntimeException("invalid Entry");

        if (output == null)
            throw new RuntimeException("Invalid Broker not initialized");

        //
        synchronized (syncObject) {
            if (log.isDebugEnabled()) log.debug("Appending " + entry);
            output.append(entry.toString()).append("\n");
            output.flush(); //write
            if (log.isDebugEnabled()) log.debug("Appended " + entry);
        }
    }
}
