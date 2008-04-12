package toolbox.tivo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractTranscoder implements ITranscoder {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private List listeners_;
 
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    protected AbstractTranscoder() {
        listeners_ = new ArrayList(1);
    }
    
    // -------------------------------------------------------------------------
    // ITranscoder Interface
    // -------------------------------------------------------------------------
    
    public void addTranscoderListener(ITranscoderListener listener) {
        listeners_.add(listener);
    }

    public void removeTranscoderListener(ITranscoderListener listener) {
        listeners_.remove(listener);
    }
    
    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    protected final void fireTranscodeStarted() {
        
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            ITranscoderListener listener = (ITranscoderListener) i.next();
            listener.transcodeStarted(this);
        }
    }
    
    protected final void fireTranscodeFinished() {
        
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            ITranscoderListener listener = (ITranscoderListener) i.next();
            listener.transcodeFinished(this);
        }
    }
    
    protected final void fireTranscodeError() {
        
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            ITranscoderListener listener = (ITranscoderListener) i.next();
            listener.transcodeError(this);
        }
    }
    
    protected final void fireTranscodeProgress() {
        
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            ITranscoderListener listener = (ITranscoderListener) i.next();
            listener.transcodeProgress(this);
        }
    }
}
