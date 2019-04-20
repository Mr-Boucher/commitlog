This software is a commit log with multiple writer and reader threads as well as a partition/CID tail executable.
The commit log is written to a text file "commit.log" by default, it can be changed using the config.properties.
The term partition is equivalent to CID.

The default writer will write the following text
The default reader and the tail partition will display that text to the console.

Advanced option if the set the Data as an only a number it will be incremented than the updated value will be displayed. Note the tail program does not do the incrementation. 
Use: 
partition_id.Number.writer.type=number 

To keep things simple each entry in the commit log uses the following structure:
    CID: <Unique-ID>: Data <CRLF>
    
Where
    CID - the commit entry ID
    Unique-ID - a unique identifier for a commit log entry
    Data - an arbitrary string that represents data
    
    Example entries are:
        A: 120: Some sample data was written
        B: 99: Sample Entry for CID=B
        A: 121: Transaction was committed here
        B: 100: Some other sample data being logged

How to use:
1) Go to the directory commitlog/target 
2) start reader using "java -jar commitlog-reader-jar-with-dependencies.jar"
3) start writer using "java -jar commitlog-writer-jar-with-dependencies.jar"
4) Extra program to tail commit log by partition "java -jar commitlog-tail-jar-with-dependencies.jar " A or B

To modify the defaults:
add/update a config.properties in the target directory see the exist resources config.properties for format 

How to build
The src is a standard mvn project build from the commitlog directory using mvn.

Things left to do:
1) More tests
2) Add negative test cases
3) Handle clean up of threads better at shutdown
4) Handle clean up of files handles better as shutdown

