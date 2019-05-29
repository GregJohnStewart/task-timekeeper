package com.gjs.taskTimekeeper.backend.crudAction;

import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration to describe what action to take and what arguments to pass to do that action.
 */
public class ActionConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionConfig.class);

	@Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
	private boolean showHelp = false;

	@Option(name = "-q", aliases = {"q", "--quit", "quit"}, usage = "Exits Management mode.")
	private boolean quit = false;

	@Option(name = "-a", aliases = {"a", "--action", "action"}, usage = "The action to take.")
	private Action action;

	@Option(name = "-o", aliases = {"o", "--object", "object"}, usage = "The type of object to operate on.")
	private KeeperObjectType objectOperatingOn;

	@Option(name = "-s", aliases = {"s", "--special", "special"}, usage = "A special command to make using the program easier.")
	private String specialAction = null;

	@Option(name="-i", aliases = {"i", "--index", "index"}, usage = "The index of the object in a view.")
	private Integer index = null;

	@Option(name="-n", aliases = {"n", "--name", "name", "-tn", "tn", "--taskName", "taskName"}, usage = "The name of the object to operate on.")
	private String name = null;
	@Option(name="-nn", aliases = {"nn", "--newName", "newName"}, usage = "The new name of the object to operate on.")
	private String newName = null;

	@Option(name="-att", aliases = {"att", "--attribute", "attribute"}, usage = "The name of the attribute to deal with. For tasks and work periods.")
	private String attributeName = null;

	@Option(name="-na", aliases = {"na", "--newAttribute", "newAttribute"}, usage = "The name of the attribute to deal with. For tasks and work periods.")
	private String newAttributeName = null;

	@Option(name="-atv", aliases = {"atv", "--attributeVal", "attributeVal"}, usage = "Specifying the value of the attribute. ")
	private String attributeVal = null;

	@Option(name="-natv", aliases = {"natv", "--newAttributeVal", "newAttributeVal"}, usage = "Specifying the new value of the attribute. ")
	private String newAttributeVal = null;

	@Option(name = "-bf", aliases = {"bf", "--before", "before"}, usage = "Specifying datetime before")
	private String before = null;

	@Option(name = "-af", aliases = {"af", "--after", "after"})
	private String after = null;

	@Option(name = "-at", aliases = {"at", "--at"})
	private String at = null;

	@Option(name = "-se", aliases = {"se", "--select", "select"}, usage="Flag to select periods.")
	private boolean select = false;

	public ActionConfig setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
		return this;
	}

	public ActionConfig setQuit(boolean quit) {
		this.quit = quit;
		return this;
	}

	public ActionConfig setAction(Action action) {
		this.action = action;
		return this;
	}

	public ActionConfig setObjectOperatingOn(KeeperObjectType objectOperatingOn) {
		this.objectOperatingOn = objectOperatingOn;
		return this;
	}

	public String getSpecialAction() {
		return specialAction;
	}

	public ActionConfig setSpecialAction(String specialAction) {
		this.specialAction = specialAction;
		return this;
	}

	public Integer getIndex() {
		return index;
	}

	public ActionConfig setIndex(Integer index) {
		this.index = index;
		return this;
	}

	public String getName() {
		return name;
	}

	public ActionConfig setName(String name) {
		this.name = name;
		return this;
	}

	public String getNewName() {
		return newName;
	}

	public ActionConfig setNewName(String newName) {
		this.newName = newName;
		return this;
	}

	public Boolean getShowHelp() {
		return showHelp;
	}

	public Boolean getQuit() {
		return quit;
	}

	public Action getAction() {
		return action;
	}

	public KeeperObjectType getObjectOperatingOn() {
		return objectOperatingOn;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public ActionConfig setAttributeName(String attributeName) {
		this.attributeName = attributeName;
		return this;
	}

	public String getNewAttributeName() {
		return newAttributeName;
	}

	public ActionConfig setNewAttributeName(String newAttributeName) {
		this.newAttributeName = newAttributeName;
		return this;
	}

	public String getAttributeVal() {
		return attributeVal;
	}

	public ActionConfig setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
		return this;
	}

	public String getNewAttributeVal() {
		return newAttributeVal;
	}

	public ActionConfig setNewAttributeVal(String newAttributeVal) {
		this.newAttributeVal = newAttributeVal;
		return this;
	}

	public String getBefore() {
		return before;
	}

	public ActionConfig setBefore(String before) {
		this.before = before;
		return this;
	}

	public String getAfter() {
		return after;
	}

	public ActionConfig setAfter(String after) {
		this.after = after;
		return this;
	}

	public String getAt() {
		return at;
	}

	public ActionConfig setAt(String at) {
		this.at = at;
		return this;
	}

	public boolean isSelect() {
		return select;
	}

	public ActionConfig setSelect(boolean select) {
		this.select = select;
		return this;
	}
}
