package toolbox.util.io;

import java.io.IOException;
import java.io.OutputStream;

public class DetachableOutputStream extends OutputStream {

    private OutputStream delegate_;
    private boolean detached_;
    
    public DetachableOutputStream(OutputStream outputStream, boolean detached) {
        delegate_ = outputStream;
        setDetached(detached);
    }

    public void write(int b) throws IOException {
        if (!detached_)
            delegate_.write(b);
    }

    public void close() throws IOException {
        if (!detached_)
            delegate_.close();
    }
    
    public void flush() throws IOException {
        if (!detached_)
            delegate_.flush();
    }
    
    public void setDetached(boolean b) {
        detached_ = b;
    }
    
    public boolean isDetached() {
        return detached_;
    }
}
