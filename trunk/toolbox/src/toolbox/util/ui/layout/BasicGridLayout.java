package toolbox.util.ui.layout;

import java.awt.Component;
import java.awt.Container;  
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * BasicGridLayout
 */
public class BasicGridLayout extends ConstraintLayout
{
    private int hGap_, vGap_;
    private int rows_, cols_, reqRows_, reqCols_;
    private int[] rowHeights_, colWidths_;
    protected int alignment_ = Alignment.NORTHWEST;
    protected int fill_ = Alignment.FILL_BOTH;
    private int colWeight_ = 0;
    private int rowWeight_ = 0;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public BasicGridLayout()
    {
        this(0, 1, 2, 2);
    }

    public BasicGridLayout(int rows, int cols)
    {
        this(rows, cols, 2, 2);
    }

    public BasicGridLayout(int rows, int cols, int hGap, int vGap)
    {
        this(rows, cols, hGap, vGap, 0, 0);
    }

    public BasicGridLayout(
        int rows,
        int cols,
        int hGap,
        int vGap,
        int hMargin,
        int vMargin)
    {
        reqRows_ = rows;
        reqCols_ = cols;
        hGap_ = hGap;
        vGap_ = vGap;
        hMargin_ = hMargin;
        vMargin_ = vMargin;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void setColumns(int cols)
    {
        if (cols < 1)
            cols = 1;
        this.cols_ = cols;
    }

    public void setRows(int rows)
    {
        if (rows < 1)
            rows = 1;
        this.rows_ = rows;
    }

    public int getRows()
    {
        return rows_;
    }

    public int getColumns()
    {
        return cols_;
    }

    public void setAlignment(int a)
    {
        alignment_ = a;
    }

    public int getAlignment()
    {
        return alignment_;
    }

    public void setFill(int f)
    {
        fill_ = f;
    }

    public int getFill()
    {
        return fill_;
    }

    public void setColWeight(int colWeight)
    {
        this.colWeight_ = colWeight;
    }

    public int getColWeight()
    {
        return colWeight_;
    }

    public void setRowWeight(int rowWeight)
    {
        this.rowWeight_ = rowWeight;
    }

    public int getRowWeight()
    {
        return rowWeight_;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Override this to set alignment on a per-component basis.
     */
    protected int alignmentFor(Component c, int row, int col)
    {
        return alignment_;
    }

    /**
     * Override this to set fill on a per-component basis.
     */
    protected int fillFor(Component c, int row, int col)
    {
        return fill_;
    }

    /**
     * Override this to set weights on a per-row basis.
     */
    protected int getRowWeight(int row)
    {
        return getRowWeight();
    }

    /**
     * Override this to set weights on a per-column basis.
     */
    protected int getColWeight(int col)
    {
        return getColWeight();
    }

    protected int sumArray(int[] array, int spacing, int size)
    {
        int i, total = 0;

        for (i = 0; i < size; i++)
            total += array[i];
        if (size > 1)
            total += (size - 1) * spacing;
        return total;
    }

    protected void calcCellSizes(Container target, int type)
    {
        int i;
        int count = target.getComponentCount();

        if (reqCols_ <= 0)
        {
            rows_ = reqRows_;
            cols_ = (count + reqRows_ - 1) / reqRows_;
        }
        else
        {
            rows_ = (count + reqCols_ - 1) / reqCols_;
            cols_ = reqCols_;
        }

        colWidths_ = new int[cols_];
        for (i = 0; i < cols_; i++)
            colWidths_[i] = 0;
        rowHeights_ = new int[rows_];
        for (i = 0; i < rows_; i++)
            rowHeights_[i] = 0;

        int index = 0;
        for (i = 0; i < count; i++)
        {
            Component c = target.getComponent(i);
            if (includeComponent(c))
            {
                Dimension size = getComponentSize(c, type);
                int row = index / cols_;
                int col = index % cols_;
                if (colWidths_[col] < size.width)
                    colWidths_[col] = size.width;
                if (rowHeights_[row] < size.height)
                    rowHeights_[row] = size.height;
                index++;
            }
        }

        Dimension size = target.getSize();
        Insets insets = target.getInsets();
        int totalWeight, totalSize, remainder;

        size.width -= insets.left + insets.right + 2 * hMargin_;
        size.height -= insets.top + insets.bottom + 2 * vMargin_;
        totalWeight = totalSize = 0;
        for (i = 0; i < cols_; i++)
        {
            totalWeight += getColWeight(i);
            totalSize += colWidths_[i];
            if (i != 0)
                totalSize += hGap_;
        }
        if (totalWeight != 0 && totalSize < size.width)
        {
            remainder = size.width - totalSize;
            for (i = 0; i < cols_; i++)
                colWidths_[i] += remainder * getColWeight(i) / totalWeight;
        }

        totalWeight = totalSize = 0;
        for (i = 0; i < rows_; i++)
        {
            totalWeight += getRowWeight(i);
            totalSize += rowHeights_[i];
            if (i != 0)
                totalSize += vGap_;
        }
        if (totalWeight != 0 && totalSize < size.height)
        {
            remainder = size.height - totalSize;
            for (i = 0; i < rows_; i++)
                rowHeights_[i] += remainder * getRowWeight(i) / totalWeight;
        }
    }

    public void measureLayout(Container target, Dimension dimension, int type)
    {
        if (dimension != null)
        {
            calcCellSizes(target, type);
            dimension.width = sumArray(colWidths_, hGap_, cols_);
            dimension.height = sumArray(rowHeights_, vGap_, rows_);
            rowHeights_ = colWidths_ = null;
        }
        else
        {
            int count = target.getComponentCount();
            if (count > 0)
            {
                Insets insets = target.getInsets();
                Dimension size = target.getSize();
                int index = 0;

                calcCellSizes(target, type);

                for (int i = 0; i < count; i++)
                {
                    Component c = target.getComponent(i);
                    if (includeComponent(c))
                    {
                        Dimension d = getComponentSize(c, type);
                        Rectangle r = new Rectangle(0, 0, d.width, d.height);
                        int row = index / cols_;
                        int col = index % cols_;
                        int x, y;

                        x = insets.left + 
                            sumArray(colWidths_, hGap_, col) + 
                            hMargin_;
                            
                        y = insets.top + 
                            sumArray(rowHeights_, vGap_, row) + 
                            vMargin_;
                            
                        if (col > 0)
                            x += hGap_;
                            
                        if (row > 0)
                            y += vGap_;
                            
                        Rectangle cell = new Rectangle(
                            x, y, colWidths_[col], rowHeights_[row]);
                            
                        Alignment.alignInCell(
                            r,
                            cell,
                            alignmentFor(c, row, col),
                            fillFor(c, row, col));
                            
                        c.setBounds(r.x, r.y, r.width, r.height);
                        index++;
                    }
                }
            }
            rowHeights_ = colWidths_ = null;
        }
    }
}
