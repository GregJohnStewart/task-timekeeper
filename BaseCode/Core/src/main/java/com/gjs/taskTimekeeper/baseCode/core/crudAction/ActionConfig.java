package com.gjs.taskTimekeeper.baseCode.core.crudAction;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.Option;

/**
 * Configuration to describe what action to take and what arguments to pass to do that action.
 */
@NoArgsConstructor
@Data
public class ActionConfig {
	
	public ActionConfig(KeeperObjectType type, Action action) {
		this();
		this.setObjectOperatingOn(type);
		this.setAction(action);
	}
	
	@Option(
		name = "-h",
		aliases = {"--help"},
		usage = "Show this help dialogue.")
	private boolean showHelp = false;
	
	@Option(
		name = "-q",
		aliases = {"q", "--quit", "quit"},
		usage = "Exits Management mode.")
	private boolean quit = false;
	
	@Option(
		name = "-sa",
		aliases = {"sa", "--save", "save"},
		usage = "Saves the changed time manager in management mode.")
	private boolean save = false;
	
	@Option(
		name = "-a",
		aliases = {"a", "--action", "action"},
		usage = "The action to take.")
	private Action action;
	
	@Option(
		name = "-o",
		aliases = {"o", "--object", "object"},
		usage = "The type of object to operate on.")
	private KeeperObjectType objectOperatingOn;
	
	@Option(
		name = "-s",
		aliases = {"s", "--special", "special"},
		usage = "A special command to make using the program easier.")
	private String specialAction = null;
	
	@Option(
		name = "-i",
		aliases = {"i", "--index", "index"},
		usage = "The index of the object in a view.")
	private Integer index = null;
	
	@Option(
		name = "-n",
		aliases = {"n", "--name", "name", "-tn", "tn", "--taskName", "taskName"},
		usage = "The name of the object to operate on.")
	private String name = null;
	
	@Option(
		name = "-nn",
		aliases = {"nn", "--newName", "newName"},
		usage = "The new name of the object to operate on.")
	private String newName = null;
	
	@Option(
		name = "-att",
		aliases = {"att", "--attribute", "attribute"},
		usage = "The name of the attribute to deal with. For tasks and work periods.")
	private String attributeName = null;
	
	@Option(
		name = "-na",
		aliases = {"na", "--newAttribute", "newAttribute"},
		usage = "The name of the attribute to deal with. For tasks and work periods.")
	private String newAttributeName = null;
	
	@Option(
		name = "-atv",
		aliases = {"atv", "--attributeVal", "attributeVal"},
		usage = "Specifying the value of the attribute. ")
	private String attributeVal = null;
	
	@Option(
		name = "-natv",
		aliases = {"natv", "--newAttributeVal", "newAttributeVal"},
		usage = "Specifying the new value of the attribute. ")
	private String newAttributeVal = null;
	
	@Option(
		name = "-bf",
		aliases = {"bf", "--before", "before"},
		usage = "Specifying datetime before")
	private String before = null;
	
	@Option(
		name = "-af",
		aliases = {"af", "--after", "after"},
		usage = "Specifying datetime after")
	private String after = null;
	
	@Option(
		name = "-at",
		aliases = {"at", "--at"},
		usage = "Specifying a datetime")
	private String at = null;
	
	@Option(
		name = "-se",
		aliases = {"se", "--select", "select"},
		usage = "Flag to select periods.")
	private boolean select = false;
	
	@Option(
		name = "-st",
		aliases = {"st", "--start", "start"},
		usage = "Specifying datetime something starts at.")
	private String start = null;
	
	@Option(
		name = "-en",
		aliases = {"en", "--end", "end"},
		usage = "Specifying datetime something ends at.")
	private String end = null;
	
	@Option(
		name = "--atts",
		usage =
			"Specifies attribute value pairs, typically overwriting existing ones. Format: att,val;att2,val2")
	private String attributes = null;
}
