package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.ADD;
import static com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel.DEFAULT;

/** Handles doing the special tasks */
public class SpecialDoer extends ActionDoer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecialDoer.class);

    private final CrudOperator operator;

    public SpecialDoer(CrudOperator operator) {
        super(operator.getManager());
        this.operator = operator;
    }

    public SpecialDoer(CrudOperator operator, Outputter outputter) {
        super(operator.getManager(), outputter);
        this.operator = operator;
    }

    /**
     * Processes special commands from the command line.
     *
     * @param config The configuration from command line
     * @return If the manager was changed in any way.
     */
    public boolean processSpecial(ActionConfig config) {
        switch (config.getSpecialAction().toLowerCase()) {
            case "completespans":
                {
                    return this.completeSpansInSelected();
                }
            case "newspan":
                return this.completeOldSpansAndAddNewInSelected(config);
            case "selectnewest":
                {
                    if (manager.getWorkPeriods().isEmpty()) {
                        LOGGER.warn("No periods to select.");
                        outputter.errorPrintln("No periods to select.");
                        return false;
                    }
                    ActionConfig actionConfig =
                            new ActionConfig()
                                    .setObjectOperatingOn(KeeperObjectType.PERIOD)
                                    .setAction(Action.VIEW)
                                    .setIndex(1)
                                    .setSelect(true);
                    return this.operator.doObjAction(actionConfig);
                }
            case "newperiod":
                {
                    boolean result = this.completeSpansInSelected();
                    ActionConfig actionConfig =
                            new ActionConfig()
                                    .setObjectOperatingOn(KeeperObjectType.PERIOD)
                                    .setAction(ADD)
                                    .setSelect(true);
                    return this.operator.doObjAction(actionConfig) || result;
                }
                // TODO:: "lastWeeksPeriods"
                // TODO:: "thisWeeksPeriods"
                // TODO:: clearPeriods
                // TODO:: clearAll
                // TODO:: cleanTasks
                // TODO:: taskStats -> view amount of time spent on what tasks in a period.
            default:
                LOGGER.error("No valid special command given.");
                this.outputter.errorPrintln("No valid special command given.");
                return false;
        }
    }

    /**
     * Finishes all spans in selected work period.
     *
     * @return True if any spans were actually finished.
     */
    private boolean completeSpansInSelected() {
        WorkPeriod selected = this.operator.getSelectedWorkPeriod();
        if (selected == null) {
            LOGGER.error("No work period selected.");
            this.outputter.errorPrintln("No work period selected.");
            return false;
        }

        if (selected.isUnCompleted()) {
            LOGGER.debug("Attempting to finish spans in selected periods.");
            this.outputter.normPrintln(DEFAULT, "Attempting to finish spans in selected periods.");
            int finishedCount = 0;
            LocalDateTime now = LocalDateTime.now();
            for (Timespan span : selected.getUnfinishedTimespans()) {
                if (!span.hasStartTime()) {
                    continue;
                }
                if (!span.hasEndTime()) {
                    if (span.getStartTime().isAfter(now)) {
                        span.setEndTime(span.getStartTime().plusSeconds(1));
                    } else {
                        span.setEndTime(LocalDateTime.now());
                    }

                    finishedCount++;
                }
            }
            if (finishedCount > 0) {
                LOGGER.info("Finished {} spans.", finishedCount);
                this.outputter.normPrintln(DEFAULT, "Finished " + finishedCount + " spans.");
                return true;
            }
        }
        return false;
    }

    /**
     * Finishes all old spans in selected work period and starts a new work period.
     *
     * @param config The configuration used to do the action.
     * @return If the manager was changed during the operation.
     */
    private boolean completeOldSpansAndAddNewInSelected(ActionConfig config) {
        LOGGER.info("Setting up config for adding a span.");
        WorkPeriod selected = this.operator.getSelectedWorkPeriod();
        if (selected == null) {
            LOGGER.error("No work period selected.");
            outputter.errorPrintln("No work period selected.");
            return false;
        }
        Name taskName;
        try {
            taskName = new Name(config.getName());
        } catch (Exception e) {
            LOGGER.error("Bad task name given: ", e);
            outputter.errorPrintln("Bad task name given: " + e.getMessage());
            return false;
        }
        Task task = manager.getTaskByName(taskName);
        if (task == null) {
            LOGGER.error("No task with name specified.");
            outputter.errorPrintln("No task with name specified.");
            return false;
        }
        // finish unfinished spans
        this.completeSpansInSelected();

        selected.addTimespan(new Timespan(task, LocalDateTime.now()));
        outputter.normPrintln(DEFAULT, "Added new timespan after finishing the existing ones.");
        return true;
    }
}
