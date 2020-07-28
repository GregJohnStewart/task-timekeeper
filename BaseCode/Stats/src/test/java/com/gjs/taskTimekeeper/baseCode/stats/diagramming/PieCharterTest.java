package com.gjs.taskTimekeeper.baseCode.stats.diagramming;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * TODO:: figure out how to test better
 */
public class PieCharterTest {
	
	@Test
	public void test() {
		PercentStats<Task> results =
			new PercentStats<>(
				new HashMap<Task, Number>() {
					{
						put(new Task("task One"), 10.0);
						put(new Task("task Two"), 90.0);
						put(new Task("task Three"), 90.0);
						put(new Task("task four"), 90.0);
						put(new Task("task five"), 90.0);
						put(new Task("task six"), 90.0);
					}
				});
		
		PieCharter<Task> charter = new PieCharter<>("Testing Pie Charting", 500, 500);
		
		BufferedImage image = charter.getChartImage(results);
		image.getHeight();
	}
}
