package toolbox.util.reflect;


/**
 * CompountPattern
 */
public class CompoundPattern extends ParamPattern
{
    protected ParamPattern pp1;
    protected ParamPattern pp2;

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

        if (pp1 instanceof CompoundPattern
            && ((CompoundPattern) pp1).pp2.getFactor(paramType) < pp2.getFactor(paramType))
        {
            CompoundPattern cp1 = (CompoundPattern) pp1;
            this.pp1 = new CompoundPattern(cp1.pp1, pp2);
            this.pp2 = cp1.pp2;
        }
        else
        {
            this.pp1 = pp1;
            this.pp2 = pp2;
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
        if (pp1.isApplicable(aClass))
            return pp1.getFactor(aClass);
        else if (pp2.isApplicable(aClass))
            return pp2.getFactor(aClass);
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
        return pp1.isApplicable(aClass) || pp2.isApplicable(aClass);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected Object advancedConvert(Object object)
    {
        return pp1.isApplicable(object.getClass()) ? pp1.convert(object) : pp2.convert(object);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected ParamPattern newPattern(Class aClass)
    {
        return new CompoundPattern(pp1.newPattern(aClass), pp2.newPattern(aClass));
    }
}