package toolbox.util;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Utility class for doubles
 */
public class DoubleUtil
{

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for DoubleUtil.
     */
    private DoubleUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Determines if the contents of a string can be expressed as a double
     * 
     * @param   s   String to inspect
     * @return  True if string contains a double, false otherwise
     */
    public static boolean isDouble(String s)
    {
        boolean b = false;
        
        if (StringUtil.isNullEmptyOrBlank(s))
            b = false;
        else
        {
            try
            {
                Double.parseDouble(s);
                b = true;
            }
            catch (NumberFormatException nfe)
            {
                b = false;
            }
        }
        
        return b;
    }


    /**
     * Computes the average of a double array
     * 
     * @param   d   Array of doubles
     * @return  Average of the array
     */
    public static double average(double[] d) 
    {
        if (d.length == 0)
            return 0;
            
        double sum = 0;
        
        for (int i=0; i<d.length; i++)
            sum+=d[i];

        double avg = (double)sum/(double)d.length;
        return avg;
    }


    /**
     * Calculates difference of two arrays of doubles
     * 
     * @param  subtractFrom  Array to subtract from
     * @param  subtract      Array to subtract
     * @return Array of the difference
     */
    public static double[] difference(double[] subtractFrom, double[] subtract) 
    {
        Assert.equals(subtractFrom.length, subtract.length, 
            "Arrays must be of equal length.");

        double[] diff = new double[subtract.length];

        for (int i = 0; i < subtract.length; i++)
            diff[i] = subtractFrom[i] - subtract[i];

        return diff;
    }


    /**
     * Determines the number of occurrences of a given value in an array
     *
     * @param  value   Value to match
     * @param  arr     Array of doubles
     * @return Number of times that value occurs in array
     */
    public static int occurs(double value, double[] arr) 
    {
        int cnt = 0;
        
        for(int i=0; i<arr.length; i++)
            if(arr[i] == value)
                ++cnt;
                
        return cnt;
    }


    /**
     * Calculates the median value
     * 
     * @param   array   Array of values
     * @return  Median value
     */
    public static double median(double[] array) 
    {
        double median;
        int len = array.length;

        switch (len) 
        {
            case 0 :
                throw new IllegalArgumentException(
                    "Cannot calculate the median of an empty set");

            case 1 :
                median = array[0];
                break;

            default :
                if (MathUtil.isEven(len))
                {
                    // Even
                    median = (array[len / 2] + array[(len / 2) - 1]) / 2;
                }
                else  
                {
                    // Odd 
                    median = array[(len - 1) / 2];
                }
        }

        return median;
    }


    /**
     * Calculates the sum of an array of doubles
     *
     * @param   d   Array of doubles
     * @return  Sum of elements in d
     */
    public static double sum(double[] d) 
    {

        if( d.length == 0)
            return 0;
            
        double sum = 0;
        
        for(int i=0; i<d.length; i++)
            sum+=d[i];

        return sum;
    }


    /**
     * Rounds a double using the given Decimal format
     * 
     * @param   d       Double to round
     * @param   format  Format to use for rounding
     * @return  Rounded value as a string
     */
    public static String round(double d, DecimalFormat format)
    {
        try 
        {
            if (Double.isNaN(d))
                d = 0.0;
            
            Number n = format.parse(d+"");
            double c = n.doubleValue();
            String s = format.format(c);
            return s;
        }
        catch (ParseException e) 
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    /**
     * Rounds a string that contains a double value 
     * 
     * @param   s       Double value as a string
     * @param   format  Format to use for rounding
     * @return  Rounded value as a string
     */
    public static String round(String s, DecimalFormat format) 
    {
        return round(Double.parseDouble(s), format);
    }
    
    
    /**
     * Determines if a double is in a range [a..b]
     * (inclusive of the start and end)
     * 
     * @param   number      Double to check
     * @param   rangeBegin  Start of the range 
     * @param   rangeEnd    End of the range   
     * @return  True if the given double is in the range, false otherwise 
     */
    public static boolean isBetween(double number, double rangeBegin, 
        double rangeEnd) 
    {
        return ((number >= rangeBegin) && (number <= rangeEnd));
    }
}
