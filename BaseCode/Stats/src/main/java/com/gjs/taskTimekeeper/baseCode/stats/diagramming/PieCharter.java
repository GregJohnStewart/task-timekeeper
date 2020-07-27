package com.gjs.taskTimekeeper.baseCode.stats.diagramming;

import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.util.Locale;
import java.util.Map;

/**
 * Charter to make pie charts
 *
 * @param <T>
 */
public class PieCharter <T> extends Charter<PercentStats<T>, PieDataset> {
	private static final String LABEL_FORMAT = "%s (%s%.2f%%)";
	
	public PieCharter(String title, int height, int width) {
		super(title, height, width);
	}
	
	@Override
	public JFreeChart getChart(PercentStats<T> results) {
		JFreeChart chart =
			ChartFactory.createPieChart(
				this.title,
				this.getDataset(results),
				false, // chart comes with labels that can't be removed, making legend
				// redundant
				true,
				Locale.US
			);
		
		return chart;
	}
	
	@Override
	protected PieDataset getDataset(PercentStats<T> results) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		Map<T, String> strings = results.getValueStrings();
		for(Map.Entry<T, Double> entry : results.getPercentages().entrySet()) {
			String label =
				(strings.containsKey(entry.getKey()) ? strings.get(entry.getKey()) + "/" : "");
			dataset.setValue(
				String.format(LABEL_FORMAT, entry.getKey().toString(), label, entry.getValue()),
				entry.getValue()
			);
		}
		
		return dataset;
	}
}
