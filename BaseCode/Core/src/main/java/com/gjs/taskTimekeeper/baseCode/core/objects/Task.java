package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Defines a task to associate to spans of time.
 *
 * <p>Examples would be to associate a certain charge number with that span of time.
 *
 * <p>Uses user defined attributes to customize it for what they need it to say.
 * <p>
 * TODO:: investigate using lombok
 */
public class Task extends KeeperObject implements Comparable<Task> {
	/**
	 * The name of the task.
	 */
	private Name name;
	/**
	 * Any attributes the user wants to add (charge number, for example)
	 */
	private Map<String, String> attributes = new HashMap<>();
	
	/**
	 * Constructor to set the name of the task.
	 *
	 * @param name The name of the task.
	 * @throws NullPointerException if the name given is null.
	 */
	public Task(Name name) throws NullPointerException {
		this.setName(name);
	}
	
	/**
	 * Constructor to set the name of the task with a bare String.
	 *
	 * @param name The name to give the task
	 * @throws NullPointerException     If the string given is null
	 * @throws IllegalArgumentException if the name is invalid. See {@link #setName(Name)}
	 */
	public Task(String name) throws NullPointerException, IllegalArgumentException {
		this.setName(new Name(name));
	}
	
	/**
	 * Constructor to set name and attributes.
	 *
	 * @param name       The name of the task.
	 * @param attributes The attributes for the task.
	 * @throws IllegalArgumentException if the name is invalid. See {@link #setName(Name)}
	 */
	@JsonCreator
	public Task(
		@JsonProperty("name")
			Name name,
		@JsonProperty("attributes")
			Map<String, String> attributes
	)
		throws NullPointerException {
		this(name);
		this.setAttributes(attributes);
	}
	
	/**
	 * Constructor to set name and attributes.
	 *
	 * @param name       The name of the task.
	 * @param attributes The attributes for the task.
	 * @throws IllegalArgumentException if the name is invalid. See {@link #setName(Name)}
	 */
	public Task(String name, Map<String, String> attributes)
		throws NullPointerException, IllegalArgumentException {
		this(name);
		this.setAttributes(attributes);
	}
	
	/**
	 * Gets the name of the task.
	 *
	 * @return The name of the task.
	 */
	public Name getName() {
		return name;
	}
	
	/**
	 * Sets the name of the task
	 *
	 * @param name The new name of the task.
	 * @return This task object.
	 * @throws NullPointerException     if the string given is null.
	 * @throws IllegalArgumentException If the string given is blank or just whitespace.
	 */
	public Task setName(Name name) throws NullPointerException, IllegalArgumentException {
		if(name == null) {
			throw new NullPointerException("Name cannot be null.");
		}
		this.name = name;
		return this;
	}
	
	/**
	 * Gets the attributes for this task.
	 *
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
		if(attributes == null) {
			throw new NullPointerException("Attributes cannot be null.");
		}
		this.attributes = attributes;
		return this;
	}
	
	@Override
	public String toString() {
		return this.name.getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof Task)) {
			return false;
		}
		Task task = (Task)o;
		return getName().equals(task.getName()) && getAttributes().equals(task.getAttributes());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getName(), this.getAttributes());
	}
	
	@Override
	public int compareTo(Task task) {
		if(task == null) {
			throw new NullPointerException("cannot compare to null");
		}
		return this.name.compareTo(task.getName());
	}
	
	@Override
	public Task clone() {
		return new Task(this.name, new HashMap<>(this.attributes));
	}
}
