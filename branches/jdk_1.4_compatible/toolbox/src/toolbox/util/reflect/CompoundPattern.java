package toolbox.util.reflect;

/**
 * CompoundPattern.
 */
public class CompoundPattern extends ParamPattern
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private ParamPattern pattern1_;
    private ParamPattern pattern2_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new CompoundPattern object.
     */
    public CompoundPattern()
    {
    }


    /**
     * Creates a new CompoundPattern object.
     * 
     * @param paramType DOCUMENT ME!
     */
    public CompoundPattern(Class paramType)
    {
        super(paramType);
    }


    /**
     * Creates a new CompoundPattern object.
     * 
     * @param pp1 DOCUMENT ME!
     * @param pp2 DOCUMENT ME!
     */
    public CompoundPattern(ParamPattern pp1, ParamPattern pp2)
    {
        this(pp1.getParamType());

        if (pp1 instanceof CompoundPattern
            && ((CompoundPattern) pp1).pattern2_.getFactor(getParamType()) < pp2
                .getFactor(getParamType()))
        {
            CompoundPattern cp1 = (CompoundPattern) pp1;
            pattern1_ = new CompoundPattern(cp1.pattern1_, pp2);
            pattern2_ = cp1.pattern2_;
        }
        else
        {
            pattern1_ = pp1;
            pattern2_ = pp2;
        }
    }

    //--------------------------------------------------------------------------
    // Overrides ParamPattern
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.reflect.ParamPattern#getFactor(java.lang.Class)
     */
    protected int getFactor(Class clazz)
    {
        if (pattern1_.isApplicable(clazz))
            return pattern1_.getFactor(clazz);
        else if (pattern2_.isApplicable(clazz))
            return pattern2_.getFactor(clazz);
        else
            return ParamPattern.MATCH_NOT;
    }


    /**
     * @see toolbox.util.reflect.ParamPattern#isApplicable(java.lang.Class)
     */
    protected boolean isApplicable(Class clazz)
    {
        return pattern1_.isApplicable(clazz) || pattern2_.isApplicable(clazz);
    }


    /**
     * @see toolbox.util.reflect.ParamPattern#advancedConvert(java.lang.Object)
     */
    protected Object advancedConvert(Object object)
    {
        return 
            pattern1_.isApplicable(object.getClass()) 
                ? pattern1_.convert(object) 
                : pattern2_.convert(object);
    }


    /**
     * @see toolbox.util.reflect.ParamPattern#newPattern(java.lang.Class)
     */
    protected ParamPattern newPattern(Class clazz)
    {
        return new CompoundPattern(
            pattern1_.newPattern(clazz), 
            pattern2_.newPattern(clazz));
    }
}