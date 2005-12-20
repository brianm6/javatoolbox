package toolbox.tivo;


public interface ITranscoderListener {

    void transcodeStarted(ITranscoder transcoder);
    
    void transcodeFinished(ITranscoder transcoder);
    
    void transcodeProgress(ITranscoder transcoder);
    
    void transcodeError(ITranscoder transcoder);
}
