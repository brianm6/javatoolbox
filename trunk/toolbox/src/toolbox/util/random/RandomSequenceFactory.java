package toolbox.util.random;

import java.util.List;


public class RandomSequenceFactory
{
    public static RandomSequence create(boolean repeating, int low, int high)
    {
        return new IntSequence(low, high, repeating);
    }
    
    public static RandomSequence create(boolean repeating, List objectList)
    {
        return new ObjectSequence(objectList, repeating);
    }

    public static RandomSequence create(boolean repeating, Object[] objectList)
    {
        return new ObjectSequence(objectList, repeating);
    }
}