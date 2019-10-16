package com.gjs.taskTimekeeper.baseCode.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A name class to provide built in validation of names. Immutable.
 */
public class Name implements Comparable<Name> {
	/**
	 * Validates the name string given. Returns the validated and sanitized name.
	 *
	 * @param name The name to validate and sanitize.
	 * @return The validated and sanitized name.
	 */
	public static String validateName(String name){
		if(name == null){
			throw new IllegalArgumentException("Name cannot be null.");
		}
		String validated = name.strip();
		if(validated.isBlank()){
			throw new IllegalArgumentException("Name cannot just be whitespace.");
		}

		return validated;
	}

	/** The actual name string. */
	private final String name;

	/**
	 * Constructor to make the name. Uses {@link #validateName(String)} to validate.
	 * @param name The name string to set.
	 */
	@JsonCreator
	public Name(
		@JsonProperty("name") String name
	) {
		this.name = validateName(name);
	}

	/**
	 * Gets the actual name string. Equivalent to {@link #toString()}
	 * @return The actual name string.
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Name name1 = (Name) o;
		return Objects.equals(getName(), name1.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(Name o) {
		if (o == null) {
			throw new NullPointerException("cannot compare to null");
		}
		return this.getName().compareTo(o.getName());
	}
}
