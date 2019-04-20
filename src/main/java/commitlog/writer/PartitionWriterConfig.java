package commitlog.writer;

public class PartitionWriterConfig {
    private String id;
    private int iterations;
    private int threads;
    private String type;

    public PartitionWriterConfig(String id, int iterations, int threads, String type ) {
        this.id = id;
        this.iterations = iterations;
        this.threads = threads;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getIterations() {
        return iterations;
    }

    public int getThreads() {
        return threads;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PartitionWriterConfig{" +
                "id='" + id + '\'' +
                ", iterations=" + iterations +
                ", threads=" + threads +
                ", type=" + type +
                '}';
    }
}
