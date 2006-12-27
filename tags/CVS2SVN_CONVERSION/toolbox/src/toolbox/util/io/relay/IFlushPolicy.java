package toolbox.util.io.relay;

import java.io.OutputStream;

public interface IFlushPolicy {
    
    boolean shouldFlush(OutputStream outputStream);
}