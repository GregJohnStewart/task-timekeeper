package com.gjs.taskTimekeeper.baseCode.stats.diagramming;

import com.gjs.taskTimekeeper.baseCode.stats.results.PercentResults;
import java.util.Locale;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class PieCharter<T> extends Charter<PercentResults<T>, PieDataset> {
    private static final String LABEL_FORMAT = "%s (%.2f%%)";

    public PieCharter(String title, int height, int width) {
        super(title, height, width);
    }

    @Override
    public JFreeChart getChart(PercentResults<T> results) {
        JFreeChart chart =
                ChartFactory.createPieChart(
                        this.title,
                        this.getDataset(results),
                        false, // chart comes with labels that can't be removed, making legend
                        // redundant
                        true,
                        Locale.US);

        return chart;
    }

    @Override
    protected PieDataset getDataset(PercentResults<T> results) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<T, Double> entry : results.getPercentages().entrySet()) {
            dataset.setValue(
                    String.format(LABEL_FORMAT, entry.getKey().toString(), entry.getValue()),
                    entry.getValue());
        }

        return dataset;
    }
}
