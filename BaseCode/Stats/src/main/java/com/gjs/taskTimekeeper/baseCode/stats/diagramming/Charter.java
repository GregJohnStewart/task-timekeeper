package com.gjs.taskTimekeeper.baseCode.stats.diagramming;

import com.gjs.taskTimekeeper.baseCode.stats.stats.Stats;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

import java.awt.image.BufferedImage;

public abstract class Charter <T extends Stats, U extends Dataset> {
	protected String title;
	protected int height;
	protected int width;
	
	public Charter(String title, int height, int width) {
		this.title = title;
		this.height = height;
		this.width = width;
	}
	
	public abstract JFreeChart getChart(T results);
	
	protected abstract U getDataset(T results);
	
	public BufferedImage getChartImage(T results) {
		JFreeChart chart = this.getChart(results);
		
		return chart.createBufferedImage(this.width, this.height);
	}
	// TODO:: method to get image from chart, results
}
