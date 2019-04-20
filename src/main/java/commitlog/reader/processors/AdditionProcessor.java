package commitlog.reader.processors;

import commitlog.model.Entry;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Keep updating the value over time
 */
public class AdditionProcessor extends CommitProcessor {
    private static final Logger log = Logger.getLogger(AdditionProcessor.class.getName());
    private static AtomicLong currentValue = new AtomicLong( 0 );

    public AdditionProcessor(Entry entry) {
        super(entry);
    }

    @Override
    protected Long process(Entry entry) throws Exception {
        if( entry.getData() == null )
            throw new IllegalArgumentException( "Entry is null" );

        long value = Long.parseLong( entry.getData() );

        //
        long resultValue = currentValue.addAndGet( value );
        if( log.isDebugEnabled() ) log.debug( "Thread " + Thread.currentThread().getName() + " changed value to " + resultValue );
        Thread.sleep( 250 ); //so 1 thread does not do all the work
        return resultValue;
    }
}
