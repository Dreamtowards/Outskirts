package outskirts.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinerOutputStream extends OutputStream {

    private List<OutputStream> outputStreams;

    public CombinerOutputStream(List<OutputStream> outputStreams) {
        this.outputStreams = outputStreams;
    }

    public CombinerOutputStream(OutputStream... outputStreams) {
        this(CollectionUtils.asList(new ArrayList<>(), outputStreams));
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream os : outputStreams)
            os.write(b);
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream os : outputStreams)
            os.flush();
    }

    @Override
    public void close() throws IOException {
        for (OutputStream os : outputStreams)
            os.close();
    }

}
