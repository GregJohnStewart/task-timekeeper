package com.gjs.taskTimekeeper.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Defines a task to associate to spans of time.
 *
 * Examples would be to associate a certain charge number with that span of time.
 *
 * Uses user defined attributes to customize it for what they need it to say.
 */
public class Task {
	/** The name of the task. */
	public String name;
	/** Any attributes the user wants to add (charge number, for example) */
	private Map<String, String> attributes = new HashMap<>();

	/**
	 * Constructor to set the name of the task.
	 * @param name The name of the task.
	 * @throws NullPointerException if the string given is null.
	 */
	public Task(String name) throws NullPointerException {
		this.setName(name);
	}

	/**
	 * Constructor to set name and attribues.
	 * @param name The name of the task.
	 * @param attributes The attributes for the task.
	 */
	public Task(String name, Map<String, String> attributes) throws NullPointerException {
		this(name);
		this.setAttributes(attributes);
	}

	/**
	 * Gets the name of the task.
	 * @return The name of the task.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the task
	 * @param name The new name of the task.
	 * @return This task object.
	 * @throws NullPointerException if the string given is null.
	 */
	public Task setName(String name) throws NullPointerException {
		if(name == null){
			throw new NullPointerException("Name cannot be null.");
		}
		this.name = name;
		return this;
	}

	/**
	 * Gets the attributes for this task.
	 * @return The attributes for this task.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes for this task.
	 *
	 * @param attributes the new attributes for the task to hae.
	 * @return This task object.
	 * @throws NullPointerException if the attrubutes object is null.
	 */
	public Task setAttributes(Map<String, String> attributes) throws NullPointerException {
		if(attributes == null){
			throw new NullPointerException("Attributes cannot be null.");
		}
		this.attributes = attributes;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Task)) return false;
		Task task = (Task) o;
		return name.equals(task.name) &&
			getAttributes().equals(task.getAttributes());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, getAttributes());
	}
}
