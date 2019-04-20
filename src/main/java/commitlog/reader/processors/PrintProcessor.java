package commitlog.reader.processors;

import commitlog.model.Entry;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Print values
 */
public class PrintProcessor extends CommitProcessor {
    private static final Logger log = Logger.getLogger(PrintProcessor.class.getName());

    public PrintProcessor(Entry entry) {
        super(entry);
    }

    @Override
    protected Entry process(Entry entry) throws Exception {
        if( entry.getData() == null )
            throw new IllegalArgumentException( "Entry is null" );

        //
        if( log.isDebugEnabled() ) log.debug( "Thread " + Thread.currentThread().getName() + " is printing out commit entry " + entry );
        Thread.sleep( 250 ); //so 1 thread does not do all the work
        return entry;
    }
}
