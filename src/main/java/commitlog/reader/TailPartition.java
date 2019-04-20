package commitlog.reader;

import commitlog.broker.TheBrokerService;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class TailPartition {
    private static final Logger log = Logger.getLogger(TheBrokerService.class.getName());
    private static Properties properties = new Properties();

    /**
     *
     * @param args no args are required
     */
    public static void main( String[] args ) {
        if( args.length != 1 )
            throw new IllegalArgumentException( "Missing partition name" );

        //
        String partition = args[0];
        try {
            //load the properties
            try (InputStream input = TailPartition.class.getClassLoader().getResourceAsStream("config.properties")) {

                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                    return;
                }

                // load a properties file
                properties.load(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            File file = new File(properties.getProperty("commit_log.file_name"));
            log.info( "Reading file " + file.getAbsolutePath() );

            //loop forever to read file line by line
            try( FileReader reader = new FileReader(file );
                 BufferedReader br = new BufferedReader( reader ) ) {
                String line;
                boolean running = true;
                while (running) {
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(partition))
                            System.out.println(line);
                    }
                }
            }
        }
        catch ( Exception e )
        {
            log.error( e );
        }
    }
}
