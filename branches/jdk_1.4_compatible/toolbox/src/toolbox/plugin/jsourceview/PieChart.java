package toolbox.plugin.jsourceview;

import java.awt.BorderLayout;
import java.text.NumberFormat;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * Pie chart that visualizes source code categories.
 */
public class PieChart extends JPanel 
{
    private static final NumberFormat format = NumberFormat.getPercentInstance();

    static
    {
        format.setMaximumFractionDigits(1);
    }
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Source code statistics to graph in a pie chart.
     */
    private FileStats stats_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PieChart.
     * 
     * @param totals Total file statistics.
     */
    public PieChart(FileStats totals) 
    {
        super(new BorderLayout());
        stats_ = totals;
        
        // create a dataset...
        PieDataset dataset = createDataset();
        
        // create the chart...
        JFreeChart chart = createChart(dataset);
        
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Creates a dataset with the source code statistics.
     * 
     * @return PieDataset
     */
    protected PieDataset createDataset() 
    {
        DefaultPieDataset result = new DefaultPieDataset();
        
        int total = stats_.getTotalLines();
        
        result.setValue(
            "Comments", 
            percent(stats_.getCommentLines(), total));
        
        result.setValue(
            "Source code", 
            percent(stats_.getCodeLines(), total));
        
        result.setValue(
            "Thrown out", 
            percent(stats_.getThrownOutLines(), total));
        
        result.setValue(
            "Blank Lines", 
            percent(stats_.getBlankLines(), total));
        
        return result;
    }

    
    /**
     * Calcs percentage given a part and a whole.
     * 
     * @param top Part of whole.
     * @param bottom Whole.
     * @return float
     */
    protected float percent(float top, float bottom)
    {
        return (top / bottom);
    }
    
    
    /**
     * Creates a chart for the source code categories.
     * 
     * @param dataset The dataset.
     * @return JFreeChart
     */
    protected JFreeChart createChart(PieDataset dataset) 
    {
        JFreeChart chart = 
            ChartFactory.createPieChart3D(
                "Source Code Categories",  // chart title
                dataset,                   // data
                true,                      // include legend
                true,                      // tooltips 
                false);                    // gen urls

        chart.setBackgroundPaint(UIManager.getColor("JOptionFrame.background"));
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setForegroundAlpha(0.5f);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{1} {0}", format, format));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator("{0} {1}", format, format));
        plot.setNoDataMessage("No data to display");
        return chart;
    }
}