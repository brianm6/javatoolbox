package toolbox.util.io.relay;

import java.io.OutputStream;

public class NeverFlushPolicy implements IFlushPolicy {

    // -------------------------------------------------------------------------
    // IFlushPolicy Interface
    // -------------------------------------------------------------------------
    
    public boolean shouldFlush(OutputStream outputStream) {
        return false;
    }
}
