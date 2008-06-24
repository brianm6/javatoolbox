package toolbox.util;

/**
 * MockConstant is used by AbstractConstantTest.
 * 
 * @see toolbox.util.AbstractConstantTest
 */
public class MockConstant extends AbstractConstant
{
    public static final MockConstant ONE = new MockConstant();
    public static final MockConstant TWO = new MockConstant();

    private MockConstant()
    {
    }
}