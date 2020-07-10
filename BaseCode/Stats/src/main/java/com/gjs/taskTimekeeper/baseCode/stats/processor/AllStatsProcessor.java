package com.gjs.taskTimekeeper.baseCode.stats.processor;


import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;


public class AllStatsProcessor extends StatProcessor<AllStats> {
	private static final AllStatsProcessor INSTANCE = new AllStatsProcessor();
	
	public static AllStatsProcessor getInstance() {
		return INSTANCE;
	}
	
	@Override
	public AllStats process(TimeManager manager) throws StatProcessingException {
		//TODO
		return null;
	}
	
	public AllStats process(TimeManager manager, WorkPeriod selectedPeriod) throws StatProcessingException {
		//TODO
		return null;
	}
}
