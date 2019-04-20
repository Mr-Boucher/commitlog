package commitlog.reader;

public class PartitionReaderConfig {
    private String id;
    private int threads;

    public PartitionReaderConfig(String id, int threads) {
        this.id = id;
        this.threads = threads;
    }

    public String getId() {
        return id;
    }


    public int getThreads() {
        return threads;
    }

    @Override
    public String toString() {
        return "PartitionWriterConfig{" +
                "id='" + id + '\'' +
                ", threads=" + threads +
                '}';
    }
}
