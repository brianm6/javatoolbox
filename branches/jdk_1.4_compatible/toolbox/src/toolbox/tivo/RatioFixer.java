package toolbox.tivo;

import java.awt.Dimension;

import org.apache.log4j.Logger;

import toolbox.util.MathUtil;

/**
 * Adjusts the ratio of the input movie to match the dimensions of the target
 * video dimensions.
 */
public class RatioFixer {
    
    private static final Logger logger_ = Logger.getLogger(RatioFixer.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Dimension target_;
    private Dimension source_;
    private Dimension fixed_;
    private int pad_;
    private boolean padLR_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public RatioFixer(Dimension target, Dimension source) {
        target_ = target;
        source_ = source;
        fixed_ = new Dimension();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
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
            logger_.warn(
                "Source " 
                + source_ 
                + " is larger than the target "
                + target_);
            
            fixed_ = target_;
            padLR_ = false;
            pad_ = 0;
        }
    }
    
    public int getHeight() {
        // must be a multiple of 2 and can't be > target height
        int i = MathUtil.isOdd(fixed_.height) ? fixed_.height - 1 : fixed_.height;
        i = Math.min(i, target_.height);
        return i;
    }
    
    public int getWidth() {
        // must be a multiple of 2 and can't be > target width
        int i = MathUtil.isOdd(fixed_.width) ? fixed_.width - 1 : fixed_.width;
        i = Math.min(i, target_.width);
        return i;
    }
    
    public boolean getPadLeftRight() {
        return padLR_;
    } 
    
    public int getPad() {
        return MathUtil.isOdd(pad_) ? pad_ + 1 : pad_;
    }
}