package com.klazen.properties;

/**
 * Represents an abstract property of any type, which is backed by a static storage medium
 * like a file. 
 * 
 * @author Chuck
 *
 * @param <T> The parameter type
 */
public abstract class Prop<T> {
	T defaultValue;
	String key;
	
	public Prop(String key, T defaultValue) {
		this.defaultValue = defaultValue;
		this.key = key;
	}
	
	/**
	 * Loads the value of the property from the properties file.
	 * 
	 * @return The value of the property
	 */
	public abstract T get();
	
	/**
	 * Sets the value of the property, and stores it in the properties file.
	 * @param value The value to set on the property
	 */
	public abstract void set(T value);
}