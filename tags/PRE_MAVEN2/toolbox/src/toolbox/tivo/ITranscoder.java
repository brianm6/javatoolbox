package toolbox.tivo;

import java.io.IOException;


public interface ITranscoder {
    
    void transcode(MovieInfo movieInfo, String destFilename)
        throws IOException, InterruptedException;
    
    void addTranscoderListener(ITranscoderListener listener);
    
    void removeTranscoderListener(ITranscoderListener listener);
}
