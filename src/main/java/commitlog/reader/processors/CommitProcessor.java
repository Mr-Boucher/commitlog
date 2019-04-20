package commitlog.reader.processors;

import commitlog.model.Entry;
import commitlog.reader.ReaderManager;
import org.apache.log4j.Logger;

/**
 * Parent class for all entry/commit processors
 *
 * todo fill this out
 *
 * @param <T>
 * @param <E>
 */
public abstract class CommitProcessor<T, E extends Entry> implements Runnable {

    private static final Logger log = Logger.getLogger(ReaderManager.class.getName());
    private final Entry entry;

    /**
     * Do something use full
     *
     * @param entry
     * @return
     */
    protected abstract T process(Entry entry ) throws Exception;

    public CommitProcessor( Entry entry ) {
        this.entry = entry;
    }


    /**
     * Template algorithm pattern
     */
    @Override
    public void run() {
        try {
            process( entry );
        } catch (Exception e) {
            log.error( e );
        }
    }
}
