package commitlog.broker;

import commitlog.model.Entry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;


public class TheBrokerServiceTest {

    //todo Add more test data
    private String partition = "TestCaller";
    private String data = "This is a test";

    private BrokerService brokerService;
    private Properties properties;

    @Before
    public void setUp() throws IOException {
        properties = new Properties();
        properties.put( "commit_log.file_name", "testfile.log" );
        properties.put( "commit_log.clear_on_start", "true" );

        brokerService = TheBrokerService.getInstance();
        brokerService.init( properties );
    }

    @After
    public void cleanup() {
        brokerService.shutdown();
    }

    /**
     * Make sure that we can write to the commit log
     *
     * @throws IOException
     */
    @Test
    public void write() throws IOException {

        Entry entry = brokerService.write(partition, data );
        Assert.assertNotNull( "Entry should not be null", entry );
        Assert.assertEquals( "CallerId", partition, entry.getCid() );
        Assert.assertEquals( "Data", data, entry.getData() );
    }

    @Test
    public void noEntryTest() throws IOException {
        Entry entry = brokerService.read(partition, false);
        Assert.assertNull("Entry should not be null", entry);
    }

    @Test
    public void read() throws IOException {
        write();
        Entry entry = brokerService.read(partition, false );
        Assert.assertNotNull( "Entry should not be null", entry );
        Assert.assertEquals( "CallerId", partition, entry.getCid() );
        Assert.assertEquals( "Data", data, entry.getData() );
    }

    @Test
    public void blockingRead() throws IOException {

        //write after 1 second to test blocking
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                brokerService.write( partition, data );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        //block reading
        Entry entry = brokerService.read(partition, true );
        Assert.assertNotNull( "Entry should not be null", entry );
        Assert.assertEquals( "CallerId", partition, entry.getCid() );
        Assert.assertEquals( "Data", data, entry.getData() );
    }
}
