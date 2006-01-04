package toolbox.tivo;

import java.awt.Dimension;

import org.apache.commons.collections.Transformer;

public class VideoStreamInfoToDimensionTransformer implements Transformer{

    public Object transform(Object input){
        VideoStreamInfo info = (VideoStreamInfo) input;
        
        Dimension dim = new Dimension(
            info.getWidth().intValue(), 
            info.getHeight().intValue());
        
        return dim;
    }

}
