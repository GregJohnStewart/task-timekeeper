package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for classes that perform actions.
 */
@Slf4j
public abstract class ActionDoer {
	
	protected final TimeManager manager;
	protected Outputter outputter = new Outputter();
	
	public TimeManager getTimeManager() {
		return this.manager;
	}
	
	public void setOutputter(Outputter outputter) {
		this.outputter = outputter;
	}
	
	public Outputter getOutputter() {
		return outputter;
	}
	
	public ActionDoer(TimeManager manager) {
		if(manager == null) {
			throw new IllegalArgumentException("Manager cannot be null");
		}
		this.manager = manager;
	}
	
	public ActionDoer(TimeManager manager, Outputter outputter) {
		this(manager);
		if(outputter == null) {
			throw new IllegalArgumentException("Outputter cannot be null");
		}
		this.setOutputter(outputter);
	}
	
	protected static Map<String, String> parseAttributes(String attString)
		throws IllegalArgumentException {
		log.debug("Parsing attribute string.");
		if(attString == null) {
			log.warn("Attribute string was null.");
			throw new IllegalArgumentException("Attribute string was null.");
		}
		Map<String, String> output = new HashMap<>();
		if(!attString.equals("EMPTY")) {
			log.debug("Attribute string is {} characters long.", attString.length());
			String[] attPairs = attString.split(";");
			for(String attPairString : attPairs) {
				String[] attPair = attPairString.split(",");
				if(attPair.length != 2) {
					throw new IllegalArgumentException(
						"Bad attribute pairs given. Bad pair: " + attPairString);
				}
				output.put(attPair[0], attPair[1]);
			}
			log.debug("Resulting att map has {} pairs.", output.size());
		}
		return output;
	}
}
