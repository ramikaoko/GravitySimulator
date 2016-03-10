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

/* This class uses the free jFreeChart api and creates a diagram (in our case a line chart) that plots all events that happened in a simulation*/
@SuppressWarnings("serial")
public class ResultDialog extends JDialog {

	private final Universe universe;

	/*
	 * --- Constructor ---
	 */

	public ResultDialog(Universe universe) {
		this.universe = universe;
		initGui();
	}

	/*
	 * --- Gui ---
	 */

	/*
	 * Initializes the GUI with a title, descriptions for the x and y axis,
	 * colors, a legend, and so on
	 */
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

		/* Set the background color to white for better readability */
		chart.setBackgroundPaint(Color.white);

		/*
		 * Get a reference to the plot window for further customisation, like a
		 * background which is sligthly darker than the background
		 */
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white.darker());
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		/* Change the unit selection to integer units only */
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		/*
		 * Create the layout for our chartPanel (which is an instance of our
		 * just customized chart) and add it to the contentPane of the JDialog
		 */
		setLayout(new GridLayout(1, 1));
		final ChartPanel chartPanel = new ChartPanel(chart);
		getContentPane().add(chartPanel);
	}

	/*
	 * This method creates a dataset with the names for each event (out of
	 * universe.getNames()) and the results for each event and plots it over the
	 * given period of time each second at a time
	 */
	private XYDataset createDataset() {
		LinkedList<int[]> results = universe.getEventResults();
		String[] names = universe.getEventNames();

		final XYSeriesCollection dataset = new XYSeriesCollection();
		/* The number of all results in our result list */
		int length = results.getFirst().length;
		LinkedList<XYSeries> series = new LinkedList<>();
		for (int i = 0; i < length; i++) {
			final XYSeries seriesCopy = new XYSeries(names[i]);
			series.add(seriesCopy);
			dataset.addSeries(seriesCopy);
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
