package toolbox.plugin.jsourceview;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.data.PieDataset;

import toolbox.util.FontUtil;

/**
 * Pie chart that visualizes source code categories.
 */
public class PieChart extends JPanel 
{
    /**
     * Total statistics for all source files.
     */
    private FileStats totals_;
    
    /**
     * Creates a PieChart.
     */
    public PieChart(FileStats totals) 
    {
        super(new BorderLayout());
        totals_ = totals;
        
        // create a dataset...
        PieDataset dataset = createDataset();
        
        // create the chart...
        JFreeChart chart = createChart(dataset);
        
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        //chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanel, BorderLayout.CENTER);
    }

    
    /**
     * Creates a dataset with the source code statistics.
     * 
     * @return PieDataset
     */
    protected PieDataset createDataset() 
    {
        DefaultPieDataset result = new DefaultPieDataset();
        
        int total = totals_.getTotalLines();
        
        result.setValue("Comments", percent(totals_.getCommentLines(), total));
        result.setValue("Source code", percent(totals_.getCodeLines(), total));
        result.setValue("Thrown out", percent(totals_.getThrownOutLines(), 
            total));
        result.setValue("Blank Lines", percent(totals_.getBlankLines(), total));
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
        return (top/bottom * 100);
    }
    
    
    /**
     * Creates a chart.
     * 
     * @param dataset The dataset.
     * @return JFreeChart
     */
    private JFreeChart createChart(PieDataset dataset) 
    {
        JFreeChart chart = ChartFactory.createPieChart3D(
            "Source Code Categories",  // chart title
            dataset,                // data
            true,                   // include legend
            true,
            false);

        // set the background color for the chart...
        //chart.setBackgroundPaint(Colors.getColor("light steel blue"));
        chart.setBackgroundPaint(UIManager.getColor("JOptionFrame.background"));
        Pie3DPlot plot = (Pie3DPlot) chart.getPlot();
        //plot.setStartAngle(270);
        //plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        plot.setSectionLabelFont(FontUtil.getPreferredSerifFont());
        //plot.setExplodePercent(1, 20.0);
        //plot.setCircular(true);
        plot.setNoDataMessage("No data to display");
        return chart;
    }
}