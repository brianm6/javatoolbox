package toolbox.plugin.jsourceview;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.data.PieDataset;
import org.jfree.util.Rotation;

/**
 * Pie chart that visualizes source code categories.
 */
public class PieChart extends JPanel 
{
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
        result.setValue("Comments", totals_.getCommentLines());
        result.setValue("Source code", totals_.getCodeLines());
        result.setValue("Thrown out", totals_.getThrownOutLines());
        result.setValue("Blank Lines", totals_.getBlankLines());
        return result;
    }

    
    /**
     * Creates a  chart.
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
        //plot.setStartAngle(0);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        //plot.setCircular(true);
        plot.setNoDataMessage("No data to display");
        return chart;
    }
}