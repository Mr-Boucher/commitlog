package commitlog.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Utils {

    public static final String PARTITION_DELIMITER = "partition_id";

    private static final Logger log = Logger.getLogger(Utils.class.getName());

    /**
     * @param properties object to be filled in
     * @throws IOException
     */
    public static void loadProperties(Properties properties) throws IOException {
        //load default the properties
        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                log.error("Sorry, unable to find default config.properties");
                throw new IllegalArgumentException("Missing config.properties resource");
            }

            // load a properties file
            properties.load(input);
        }

        //load the override properties if they exist
        File file = new File("config.properties");
        if (!file.exists())
            log.info( "No extra properties defined. This is ok and just an info message" );
        else {
            try (FileReader reader = new FileReader(file);
                 BufferedReader br = new BufferedReader(reader)) {
                properties.load(br);
            }
        }
    }
}
