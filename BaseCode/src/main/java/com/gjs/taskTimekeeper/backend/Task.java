package com.gjs.taskTimekeeper.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Defines a task to associate to spans of time.
 * <p>
 * Examples would be to associate a certain charge number with that span of time.
 * <p>
 * Uses user defined attributes to customize it for what they need it to say.
 */
public class Task extends KeeperObject implements Comparable<Task>{
	/**
	 * The name of the task.
	 */
	private String name;
	/**
	 * The uuid of the task. For ensuring uniqueness and allowing for comparison.
	 * TODO:: remove this, not needed as name is now treated as unique
	 */
	private final UUID uuid;
	/**
	 * Any attributes the user wants to add (charge number, for example)
	 */
	private Map<String, String> attributes = new HashMap<>();

	/**
	 * Constructor to set the UUID.
	 *
	 * @param uuid The uuid to set.
	 * @throws NullPointerException If the uuid object is null.
	 */
	private Task(UUID uuid) throws NullPointerException {
		if (uuid == null) {
			throw new NullPointerException("UUID for task cannot be null.");
		}
		this.uuid = uuid;
	}

	/**
	 * Constructor that sets a random UUID.
	 */
	private Task() {
		this(UUID.randomUUID());
	}

	/**
	 * Constructor to set the name of the task.
	 *
	 * @param name The name of the task.
	 * @throws NullPointerException if the string given is null.
	 */
	public Task(String name) throws NullPointerException {
		this();
		this.setName(name);
	}

	/**
	 * Constructor to set name and attribues.
	 *
	 * @param name       The name of the task.
	 * @param attributes The attributes for the task.
	 */
	public Task(String name, Map<String, String> attributes) throws NullPointerException {
		this(name);
		this.setAttributes(attributes);
	}

	/**
	 * Constructor to set the UUID and name of the task.
	 *
	 * @param uuid The uuid to set
	 * @param name The name to set
	 * @throws NullPointerException if either parameter is null
	 */
	public Task(UUID uuid, String name) throws NullPointerException {
		this(uuid);
		this.setName(name);
	}

	/**
	 * Constructor to set everything.
	 *
	 * @param uuid       The uuid to set
	 * @param name       The name to set
	 * @param attributes The attributes to set
	 * @throws NullPointerException If any parameter is null
	 */
	public Task(UUID uuid, String name, Map<String, String> attributes) throws NullPointerException {
		this(uuid, name);
		this.setAttributes(attributes);
	}

	/**
	 * Gets the name of the task.
	 *
	 * @return The name of the task.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the task
	 *
	 * @param name The new name of the task.
	 * @return This task object.
	 * @throws NullPointerException if the string given is null.
	 * @throws IllegalArgumentException If the string given is blank or just whitespace.
	 */
	public Task setName(String name) throws NullPointerException, IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException("Name cannot be null.");
		}
		name = name.strip();
		if(name.isBlank()){
			throw new IllegalArgumentException("Name cannot just be whitespace.");
		}
		this.name = name;
		return this;
	}

	/**
	 * Gets the uuid of the task.
	 *
	 * @return The uuid of the task.
	 */
	public UUID getUuid() {
		return uuid;
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
		if (attributes == null) {
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
		return getName().equals(task.getName()) &&
			getUuid().equals(task.getUuid()) &&
			getAttributes().equals(task.getAttributes());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getUuid(), getAttributes());
	}

	@Override
	public int compareTo(Task task) {
		if (task == null) {
			throw new NullPointerException("cannot compare to null");
		}
		return this.uuid.compareTo(task.getUuid());
	}

	@Override
	public Task clone() {
		return new Task(this.uuid, this.name, new HashMap<>(this.attributes));
	}
}
