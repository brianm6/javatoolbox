package toolbox.util.io.relay;

import java.io.OutputStream;

public class AlwaysFlushPolicy implements IFlushPolicy {

    // -------------------------------------------------------------------------
    // IFlushPolicy Interface
    // -------------------------------------------------------------------------
    
    public boolean shouldFlush(OutputStream outputStream) {
        return true;
    }
}
