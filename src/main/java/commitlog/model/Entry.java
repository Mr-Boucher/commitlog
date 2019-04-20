package commitlog.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class Entry
{
    //Start each new run with a new time stamp
    private static AtomicLong entryIdCounter = new AtomicLong( System.currentTimeMillis() );

    private long entryId;
    private String cid;
    private String data;

    public Entry(String cid, String data) {
        this.entryId = entryIdCounter.getAndIncrement();
        this.cid = cid;
        this.data = data;
    }

    public Entry(long entryId, String cid, String data) {
        this.entryId = entryId;
        this.cid = cid;
        this.data = data;
    }

    public long getEntryId() {
        return entryId;
    }

    public String getCid() {
        return cid;
    }

    public String getData() {
        return data;
    }

    public String toString()
    {
        return cid + ": " + entryId + ": " + data;
    }
}
