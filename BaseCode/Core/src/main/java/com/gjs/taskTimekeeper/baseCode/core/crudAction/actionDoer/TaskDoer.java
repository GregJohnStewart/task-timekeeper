package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** The action doer to handle managing Tasks. */
@Slf4j
public class TaskDoer extends CrudDoer<Task> {
    public TaskDoer(TimeManager manager) {
        super(manager);
    }

    public TaskDoer(TimeManager manager, Outputter outputter) {
        super(manager, outputter);
    }

    @Override
    protected boolean add(ActionConfig config) {
        // ensure we have a name for the new task
        if (config.getName() == null) {
            log.warn("No task name given for the new task. Not adding new task.");
            outputter.errorPrintln("ERROR:: No task name given for the new task.");
            return false;
        }
        // check we aren't duplicating names
        try {
            if (manager.getTaskByName(config.getName()) != null) {
                log.warn("Duplicate task name given. Not adding new task.");
                outputter.errorPrintln("ERROR:: Duplicate task name given.");
                return false;
            }
        } catch (Exception e) {
            log.warn("Invalid task name given. Not adding new task.");
            outputter.errorPrintln("Invalid task name given. Not adding new task.");
            return false;
        }
        if (config.getAttributeName() != null && config.getAttributes() != null) {
            log.warn("Cannot process both single attribute and set of attributes.");
            outputter.errorPrintln("Cannot process both single attribute and set of attributes.");
            return false;
        }

        Task newTask = new Task(new Name(config.getName()));

        if (config.getAttributeName() != null) {
            if (config.getAttributeVal() != null) {
                log.debug("Setting attribute name and value.");
                newTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
            }
        }
        if (config.getAttributes() != null) {
            Map<String, String> newAtts;
            try {
                newAtts = ActionDoer.parseAttributes(config.getAttributes());
            } catch (IllegalArgumentException e) {
                log.warn("Attribute string given was invalid. Error: ", e);
                outputter.errorPrintln(
                    "Attribute string given was invalid. Error: " + e.getMessage());
                return false;
            }
            newTask.setAttributes(newAtts);
        }

        outputter.normPrintln(OutputLevel.VERBOSE, "New task details:");
        outputter.normPrintln(OutputLevel.VERBOSE, "\tName: " + newTask.getName());
        if (!newTask.getAttributes().isEmpty()) {
            outputter.normPrintln(
                    OutputLevel.VERBOSE,
                    "\t(custom attribute) "
                            + config.getAttributeName()
                            + ": "
                            + config.getAttributeVal());
        }

        manager.addTask(newTask);
        outputter.normPrintln(OutputLevel.DEFAULT, "New task added.");
        return true;
    }

    @Override
    protected boolean edit(ActionConfig config) {
        Task editingTask = null;
        if (config.getName() != null && config.getIndex() != null) {
            log.warn(
                "Error: Both name and search index were used to specify which task to edit.");
            outputter.errorPrintln(
                    "ERROR:: Cannot give both name and index to specify which task to edit.");
            return false;
        } else if (config.getName() != null) {
            editingTask = manager.getTaskByName(config.getName());
        } else if (config.getIndex() != null) {
            int index = config.getIndex() - 1;
            List<Task> searchResults = this.search(config);
            if (index >= 0 && index < searchResults.size()) {
                editingTask = this.search(config).get(index);
                outputter.normPrintln(
                        OutputLevel.DEFAULT, "Editing task: " + editingTask.getName());
            } else {
                log.warn("Index given was out of bounds for referencing tasks.");
                outputter.errorPrintln("ERROR: Index given was out of bounds.");
                return false;
            }
        } else {
            log.warn("No task name or index entered to look up task to change.");
            outputter.errorPrintln("ERROR:: Nothing given to specify which task to edit.");
            return false;
        }
        if (editingTask == null) {
            log.warn("No task found with name or index given.");
            outputter.errorPrintln("No task found to edit.");
            return false;
        }
        if (config.getAttributeName() != null && config.getAttributes() != null) {
            log.warn("Cannot process both single attribute and set of attributes.");
            outputter.errorPrintln("Cannot process both single attribute and set of attributes.");
            return false;
        }

        boolean modified = false;
        if (config.getNewName() != null) {
            try {
                manager.updateTaskName(editingTask, new Name(config.getNewName()));
                modified = true;
                outputter.normPrintln(
                        OutputLevel.VERBOSE, "New name set to: " + editingTask.getName());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid new name given. ", e);
                outputter.errorPrintln("Invalid new name given. Error: \n" + e.getMessage());
                return false;
            }
        }

        if (config.getAttributeName() != null) {
            if (config.getAttributeVal() != null) {
                if (!config.getAttributeVal()
                        .equals(editingTask.getAttributes().get(config.getAttributeName()))) {
                    modified = true;
                    editingTask
                            .getAttributes()
                            .put(config.getAttributeName(), config.getAttributeVal());
                    outputter.normPrintln(
                            OutputLevel.VERBOSE,
                            "Set attribute "
                                    + config.getAttributeName()
                                    + " to "
                                    + config.getAttributeVal());
                }
            } else {
                if (editingTask.getAttributes().containsKey(config.getAttributeName())) {
                    modified = true;
                    editingTask.getAttributes().remove(config.getAttributeName());
                    outputter.normPrintln(
                            OutputLevel.VERBOSE, "Removed attribute: " + config.getAttributeName());
                }
            }
        }
        if (config.getAttributes() != null) {
            Map<String, String> newAtts;
            try {
                newAtts = ActionDoer.parseAttributes(config.getAttributes());
            } catch (IllegalArgumentException e) {
                log.warn("Attribute string given was invalid. Error: ", e);
                outputter.errorPrintln(
                    "Attribute string given was invalid. Error: " + e.getMessage());
                return false;
            }
            if (!editingTask.getAttributes().equals(newAtts)) {
                editingTask.setAttributes(newAtts);
                modified = true;
            } else {
                log.debug("Attribute map given same as what was already held.");
            }
        }

        if (!modified) {
            outputter.normPrintln(OutputLevel.DEFAULT, "Task not modified.");
        }

        return modified;
    }

    @Override
    protected boolean remove(ActionConfig config) {
        Task taskToRemove = null;

        if (config.getName() != null) {
            taskToRemove = manager.getTaskByName(config.getName());
        }

        if (config.getIndex() != null) {
            taskToRemove = this.getAtIndex(config);
        }

        if (taskToRemove == null) {
            log.info("No task with the name given found or at index given.");
            outputter.errorPrintln("No task with the name given found or at index given.");
            return false;
        }

        if (!manager.getTimespansWith(taskToRemove).isEmpty()) {
            log.warn("Task part of one or more time spans. Cannot remove task.");
            outputter.errorPrintln("Task given part of one or more time spans. Cannot remove.");
            return false;
        }

        manager.getTasks().remove(taskToRemove);
        return true;
    }

    @Override
    public List<String> getViewHeaders() {
        List<String> output = new ArrayList<>();

        output.add("#");
        output.add("Name");

        return output;
    }

    @Override
    public List<String> getViewRowEntries(int rowNum, Task object) {
        List<String> output = new ArrayList<>();

        output.add("" + rowNum);
        output.add(object.getName().toString());

        return output;
    }

    @Override
    public List<Task> search(ActionConfig config) {
        List<Task> output = null;

        if (config.getName() != null) {
            output = manager.getTasksByNamePattern(config.getName());
        } else {
            output = new LinkedList<>(manager.getTasks());
        }

        if (config.getAttributeName() != null && config.getAttributeVal() != null) {
            output =
                    output.stream()
                            .filter(
                                    (Task task) -> {
                                        if (task.getAttributes()
                                                .containsKey(config.getAttributeName())) {
                                            return config.getAttributeVal()
                                                    .equals(
                                                            task.getAttributes()
                                                                    .get(
                                                                            config
                                                                                    .getAttributeName()));
                                        }
                                        return false;
                                    })
                            .collect(Collectors.toList());
        }
        Collections.reverse(output);
        return output;
    }

    @Override
    public void displayOne(Task task) {
        outputter.normPrintln(OutputLevel.DEFAULT, "Task:");
        outputter.normPrintln(OutputLevel.DEFAULT, "\tName: " + task.getName());

        if (task.getAttributes().isEmpty()) {
            outputter.normPrintln(OutputLevel.DEFAULT, "\tNo Attributes");
        } else {
            outputter.normPrintln(OutputLevel.DEFAULT, "\tAttributes:");
            for (Map.Entry<String, String> att : task.getAttributes().entrySet()) {
                outputter.normPrintln(
                        OutputLevel.DEFAULT, "\t\t" + att.getKey() + ": " + att.getValue());
            }
        }

        outputter.normPrintln(
                OutputLevel.DEFAULT,
                "\tPeriods with task: " + manager.getWorkPeriodsWith(task).size());
        outputter.normPrintln(
                OutputLevel.DEFAULT, "\tSpans with task: " + manager.getTimespansWith(task).size());
    }

    @Override
    public void view(ActionConfig config) {
        log.info("Viewing one or more tasks.");

        {
            if (config.getName() != null) {
                Task task = manager.getTaskByName(config.getName());
                if (task != null) {
                    log.debug("Found a task that matched the name.");
                    this.displayOne(task);
                    return;
                }
            } else {
                Task task = this.getAtIndex(config);

                if (task != null) {
                    log.debug("Found a task at the given index.");
                    this.displayOne(task);
                    return;
                }
            }
        }

        List<Task> results = this.search(config);

        this.printView("Tasks", results);
    }
}
