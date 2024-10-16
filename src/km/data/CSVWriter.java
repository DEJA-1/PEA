package km.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    private FileWriter writer;

    public CSVWriter(String filePath) throws IOException {
        writer = new FileWriter(filePath);
        writer.write("Algorithm,Execution Time (ns)\n");
    }

    public void writeRecord(String algorithm, long time) throws IOException {
        writer.write(algorithm + "," + time + "\n");
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}

