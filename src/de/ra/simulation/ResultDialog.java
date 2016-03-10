package de.ra.simulation;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/* TODO: Kommentare, lesbarkeit */

@SuppressWarnings("serial")
public class ResultDialog extends JDialog {

	private final Universe universe;

	public ResultDialog(Universe universe) {
		this.universe = universe;
		initGui();
	}

	private void initGui() {
		XYDataset dataset = createDataset();
		final JFreeChart chart = ChartFactory.createXYLineChart("Auswertung", // chart
																				// title
				"Zeit", // x axis label
				"Anzahl", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white.darker());
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		setLayout(new GridLayout(1, 1));
		final ChartPanel chartPanel = new ChartPanel(chart);
		getContentPane().add(chartPanel);
	}

	private XYDataset createDataset() {
		LinkedList<int[]> results = universe.getResults();
		String[] names = universe.getNames();
		final XYSeriesCollection dataset = new XYSeriesCollection();
		int length = results.getFirst().length;
		LinkedList<XYSeries> series = new LinkedList<>();
		for (int i = 0; i < length; i++) {
			final XYSeries s = new XYSeries(names[i]);
			series.add(s);
			dataset.addSeries(s);
		}

		int time = 0;
		for (int[] array : results) {
			for (int i = 0; i < array.length; i++) {
				XYSeries xy = series.get(i);
				xy.add(time, array[i]);
			}
			time++;
		}

		return dataset;
	}
}
