package toolbox.plugin.jsourceview;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

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
public class PieChart extends JPanel // extends ApplicationFrame 
{
    /**
     * Creates a PieChart.
     */
    public PieChart() 
    {
        super(new BorderLayout());
        
        // create a dataset...
        PieDataset dataset = createSampleDataset();
        
        // create the chart...
        JFreeChart chart = createChart(dataset);
        
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanel, BorderLayout.CENTER);
    }

    
    /**
     * Creates a sample dataset for the demo.
     * 
     * @return A sample dataset.
     */
    private PieDataset createSampleDataset() 
    {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Comments", new Double(10));
        result.setValue("Source code", new Double(20.0));
        result.setValue("Thrown out", new Double(40));
        result.setValue("Blank Lines", new Double(30));
        return result;
    }

    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * @return A chart.
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
        chart.setBackgroundPaint(Color.yellow);
        Pie3DPlot plot = (Pie3DPlot) chart.getPlot();
        plot.setStartAngle(270);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data to display");
        return chart;
    }

    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
//    public static void main(String[] args) {
//
//        PieChart demo = new PieChart("Pie Chart 3D Demo 1");
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//
//    }

}