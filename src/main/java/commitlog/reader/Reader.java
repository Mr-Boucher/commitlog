package commitlog.reader;

import commitlog.broker.BrokerService;
import commitlog.model.Entry;
import commitlog.reader.processors.AdditionProcessor;
import commitlog.reader.processors.PrintProcessor;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reader of the commit long it hands the processing off to other threads using the Executor services
 */
public class Reader implements Runnable{
    private static final Logger log = Logger.getLogger(Reader.class.getName());

    private final BrokerService broker;
    private final HashMap<String, ExecutorService> executors;
    private AtomicBoolean running = new AtomicBoolean();

    public Reader(BrokerService broker, HashMap<String, ExecutorService> executors) {
        this.broker = broker;
        this.executors = executors;
        log.debug( "Executors count:" + executors.size() );
    }

    public void shutdown() {
        running.set( false );
    }

    /**
     * Read all the commits and hand them off to processors of the correct type
     */
    @Override
    public void run() {
        running.set( true );
        try {
            do {

                //read next entry in log
                Entry entry = broker.read(null, true);
                log.debug("Read entry " + entry);

                //
                ExecutorService executor = executors.get(entry.getCid());

                //todo make this configurable
                try {
                    Long.parseLong( entry.getData() );
                    //if this works the use the Addition Processor
                    executor.execute(new AdditionProcessor(entry));
                }
                catch( NumberFormatException npe ) {
                    //if it is not a long then use the print processor
                    executor.execute(new PrintProcessor(entry));
                }
            }
            while ( running.get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
