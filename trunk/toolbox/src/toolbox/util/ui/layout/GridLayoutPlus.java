package toolbox.util.ui.layout;

import java.awt.Component;

/**
 * GridLayoutPlus
 */
public class GridLayoutPlus extends BasicGridLayout
{

    private int[] rowWeights_, colWeights_, colFlags_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public GridLayoutPlus()
    {
        super(0, 1, 2, 2);
    }

    /**
     * Creates a GridLayoutPlus
     * 
     * @param  rows     Number of rows
     * @param  cols     Number of columns
     */
    public GridLayoutPlus(int rows, int cols)
    {
        super(rows, cols, 2, 2);
    }

    /**
     * Creates a GridLayoutPlus
     * 
     * @param  rows     Number of rows
     * @param  cols     Number of columns
     * @param  hGap     Horizontal gap
     * @param  vGap     Vertical gap
     */
    public GridLayoutPlus(int rows, int cols, int hGap, int vGap)
    {
        super(rows, cols, hGap, vGap, 0, 0);
    }

    /**
     * Creates a GridLayoutPlus
     * 
     * @param  rows     Number of rows
     * @param  cols     Number of columns
     * @param  hGap     Horizontal gap
     * @param  vGap     Vertical gap
     * @param  hMargin  Horizontal margin
     * @param  vMargin  Vertical margin
     */
    public GridLayoutPlus(
        int rows,
        int cols,
        int hGap,
        int vGap,
        int hMargin,
        int vMargin)
    {
        super(rows, cols, hGap, vGap, hMargin, vMargin);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the row weight
     * 
     * @param  row      Row number
     * @param  weight   Row weight
     */
    public void setRowWeight(int row, int weight)
    {
        rowWeights_ = setWeight(rowWeights_, row, weight);
    }

    /**
     * Sets the column weight
     * 
     * @param  col      Column number
     * @param  weight   Column weight
     */
    public void setColWeight(int col, int weight)
    {
        colWeights_ = setWeight(colWeights_, col, weight);
    }

    /**
     * Sets column alignment
     * 
     * @param  col      Column number
     * @param  v        Alignment
     */
    public void setColAlignment(int col, int v)
    {
        colFlags_ = setWeight(colFlags_, col, v);
    }

    /**
     * Adds the specified named component to the layout.
     * 
     * @param   name    String name
     * @param   comp    Component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param   comp    Component to be removed
     */
    public void removeLayoutComponent(Component comp)
    {
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    private int[] setWeight(int[] w, int index, int weight)
    {
        if (w == null)
            w = new int[index + 1];
        else if (index >= w.length)
        {
            int[] n = new int[index + 1];
            System.arraycopy(w, 0, n, 0, w.length);
            w = n;
        }
        w[index] = weight;
        return w;
    }

    protected int getRowWeight(int row)
    {
        if (rowWeights_ != null && row < rowWeights_.length)
            return rowWeights_[row];
        return 0;
    }

    protected int getColWeight(int col)
    {
        if (colWeights_ != null && col < colWeights_.length)
            return colWeights_[col];
        return 0;
    }

    protected int getColAlignment(int col)
    {
        if (colFlags_ != null && col < colFlags_.length)
            return colFlags_[col];
            
        return getAlignment();
    }

    protected int alignmentFor(Component c, int row, int col)
    {
        return getColAlignment(col);
    }

    protected int fillFor(Component c, int row, int col)
    {
        return getFill();
    }

    protected int weightForColumn(int col)
    {
        return 1;
    }

    protected int weightForColumn(int row, int col)
    {
        return 1;
    }
}
