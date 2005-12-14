package toolbox.tivo;

import java.awt.Dimension;

import org.apache.log4j.Logger;


/**
 * RatioFixer is responsible for _____.
 */
public class RatioFixer {
    
    private static final Logger logger_ = Logger.getLogger(RatioFixer.class);
    
    Dimension target_;
    Dimension source_;
    Dimension fixed_;
    int pad_;
    boolean padLR_;
    
    public RatioFixer(Dimension target, Dimension source) {
        target_ = target;
        source_ = source;
        fixed_ = new Dimension();
    }
    
    public void calc() {
        
        // 416 x 304  1.3682
        //
        // 720/416 = 1.73
        // 480/304 = 1.57
        //
        // height < width so multiply width by 1.57
        //
        // 416 x 1.57 = 653
        //
        // 720 - 653 / 2 = 3.5 = 34
        // 
        // if using width multiplier use -padright -padleft
        // if using height multiplier use -padtop -padbottom
        //
        // 656 x 480  -32 + 32  1.36
        // 

        if (target_.height >= source_.height &&
            target_.width >= source_.width) {
        
            float wratio = target_.width / (float) source_.width;
            float hratio = target_.height/ (float) source_.height;
            float multiplier = Math.min(wratio, hratio);
            
            logger_.debug("Multiplier= " + multiplier);
            
            fixed_.width = (int) (source_.width * multiplier);
            fixed_.height = (int) (source_.height * multiplier);
            
            logger_.debug("Fixed = " + fixed_);
            
            if (wratio == multiplier) {
                padLR_ = false;
                pad_ = (int) ((target_.height - fixed_.height)/2);
            }
            else if (hratio == multiplier) {
                padLR_ = true;
                pad_ = (int) ((target_.width - fixed_.width)/ (int) 2);
            }
            else {
                throw new IllegalArgumentException(
                    "Multiplier doesn't match either ration");
            }
        }
        else {
            throw new IllegalArgumentException(
                "Source " + source_ + " is larget than the target " + target_);
        }
    }
    
    public int getHeight() {
        return fixed_.height;
    }
    
    public int getWidth() {
        return fixed_.width;
    }
    
    public boolean getPadLeftRight() {
        return padLR_;
    }
    
    public int getPad() {
        return pad_;
    }
}
