package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Abstract class for classes that perform actions. */
public abstract class ActionDoer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoer.class);

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

    public void setOutputLevelThreshold(OutputLevel outputLevel) {
        this.outputter.setOutputLevelThreshold(outputLevel);
    }

    public ActionDoer(TimeManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        this.manager = manager;
    }

    public ActionDoer(TimeManager manager, Outputter outputter) {
        this(manager);
        if (outputter == null) {
            throw new IllegalArgumentException("Outputter cannot be null");
        }
        this.setOutputter(outputter);
    }

    protected static Map<String, String> parseAttributes(String attString)
            throws IllegalArgumentException {
        LOGGER.debug("Parsing attribute string.");
        if (attString == null) {
            LOGGER.warn("Attribute string was null.");
            throw new IllegalArgumentException("Attribute string was null.");
        }
        Map<String, String> output = new HashMap<>();
        if (!attString.equals("EMPTY")) {
            LOGGER.debug("Attribute string is {} characters long.", attString.length());
            String[] attPairs = attString.split(";");
            for (String attPairString : attPairs) {
                String[] attPair = attPairString.split(",");
                if (attPair.length != 2) {
                    throw new IllegalArgumentException(
                            "Bad attribute pairs given. Bad pair: " + attPairString);
                }
                output.put(attPair[0], attPair[1]);
            }
            LOGGER.debug("Resulting att map has {} pairs.", output.size());
        }
        return output;
    }
}
