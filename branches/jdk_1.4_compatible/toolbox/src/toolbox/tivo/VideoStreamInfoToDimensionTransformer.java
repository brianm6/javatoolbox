package toolbox.tivo;

import java.awt.Dimension;

import org.apache.commons.collections.Transformer;

/**
 * VideoStreamInfoToDimensionTransformer is responsible for converting the 
 * height and width attributes of a {@link VideoStreamInfo} to a 
 * {@link java.awt.Dimension}.
 */
public class VideoStreamInfoToDimensionTransformer implements Transformer{

    public Object transform(Object input){
        VideoStreamInfo info = (VideoStreamInfo) input;
        
        Dimension dim = new Dimension(
            info.getWidth().intValue(), 
            info.getHeight().intValue());
        
        return dim;
    }
}
