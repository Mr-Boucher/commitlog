package commitlog.broker;

import commitlog.model.Entry;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public interface BrokerService {

    /**
     *
     * @param properties
     */
    void init(Properties properties) throws IOException;

    /**
     *
     */
    void shutdown();

    /**
     * Returns null if no data, unless blocking is true this it will wait forever
     * or until there is a new entry for the partition
     *
     * @param partition the partition to read the next line for
     * @param block wait for next entry for this partition
     *
     * @return null or Entry
     * @throws IOException
     */
    Entry read(String partition, boolean block) throws IOException;

    /**
     * This is method is synchronized
     * @param partition a unique id for the caller.
     * @param data String of data
     * @return The Entry object stored in the commit log
     */
    Entry write(String partition, String data) throws IOException;

}
