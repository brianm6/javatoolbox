package toolbox.util.reflect;


/**
 * CompoundPattern
 */
public class CompoundPattern extends ParamPattern
{
    private ParamPattern pattern1_;
    private ParamPattern pattern2_;

    // CONSTRUCTORS

    /**
     * Creates a new CompoundPattern object.
     */
    public CompoundPattern()
    {
        super();
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
        this(pp1.paramType);

        if (pp1 instanceof CompoundPattern && 
            ((CompoundPattern) pp1).pattern2_.getFactor(paramType) < 
                pp2.getFactor(paramType))
        {
            CompoundPattern cp1 = (CompoundPattern) pp1;
            this.pattern1_ = new CompoundPattern(cp1.pattern1_, pp2);
            this.pattern2_ = cp1.pattern2_;
        }
        else
        {
            this.pattern1_ = pp1;
            this.pattern2_ = pp2;
        }
    }

    // PARAMPATTERN METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected int getFactor(Class aClass)
    {
        if (pattern1_.isApplicable(aClass))
            return pattern1_.getFactor(aClass);
        else if (pattern2_.isApplicable(aClass))
            return pattern2_.getFactor(aClass);
        else
            return ParamPattern.MATCH_NOT;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean isApplicable(Class aClass)
    {
        return pattern1_.isApplicable(aClass) || pattern2_.isApplicable(aClass);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected Object advancedConvert(Object object)
    {
        return pattern1_.isApplicable(object.getClass()) ? 
               pattern1_.convert(object) : 
               pattern2_.convert(object);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected ParamPattern newPattern(Class aClass)
    {
        return new CompoundPattern(
            pattern1_.newPattern(aClass), pattern2_.newPattern(aClass));
    }
}