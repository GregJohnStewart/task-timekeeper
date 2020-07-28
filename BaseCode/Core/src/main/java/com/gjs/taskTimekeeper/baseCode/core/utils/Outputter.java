package com.gjs.taskTimekeeper.baseCode.core.utils;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel.DEFAULT;
import static com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel.NONE;

/**
 * Class to handle outputting action and other runtime information to a printstream (or other
 * outputstreams).
 */
public final class Outputter {
	private static Outputter DEFAULT_OUTPUTTER;
	
	static {
		DEFAULT_OUTPUTTER = new Outputter();
	}
	
	public static Outputter getDefaultOutputter() {
		return DEFAULT_OUTPUTTER;
	}
	
	private PrintStream MESSAGE_OUTPUT_STREAM;
	private PrintStream MESSAGE_ERROR_STREAM;
	private OutputLevel OUTPUT_LEVEL_THRESHOLD = DEFAULT;
	
	public Outputter() {
		this(System.out, System.err);
	}
	
	public Outputter(OutputLevel threshold) {
		this();
		this.setOutputLevelThreshold(threshold);
	}
	
	public Outputter(PrintStream normStream, PrintStream errStream) {
		this.setMessageOutputStream(normStream);
		this.setMessageErrorStream(errStream);
	}
	
	public Outputter(OutputLevel threshold, PrintStream normStream, PrintStream errStream) {
		this(normStream, errStream);
		this.setOutputLevelThreshold(threshold);
	}
	
	public Outputter(OutputStream normStream, OutputStream errStream) {
		this.setMessageOutputStream(normStream);
		this.setMessageErrorStream(errStream);
	}
	
	public Outputter(OutputLevel threshold, OutputStream normStream, OutputStream errStream) {
		this(normStream, errStream);
		this.setOutputLevelThreshold(threshold);
	}
	
	// <editor-fold desc="Public methods">
	
	/**
	 * Sets the threshold to use when determining whether or not to output.
	 *
	 * @param level The level to set.
	 */
	public void setOutputLevelThreshold(OutputLevel level) {
		if(level == null) {
			throw new IllegalArgumentException("Output level cannot be null");
		}
		OUTPUT_LEVEL_THRESHOLD = level;
	}
	
	/**
	 * Sets the normal output message printstream.
	 *
	 * @param stream The printstream to use for normal output.
	 */
	public void setMessageOutputStream(PrintStream stream) {
		MESSAGE_OUTPUT_STREAM = stream;
	}
	
	/**
	 * Sets the normal output message outputstream. Wraps the stream given in a PrintStream
	 *
	 * @param stream The outputstream to use for normal output.
	 */
	public void setMessageOutputStream(OutputStream stream) {
		setMessageOutputStream((stream == null ? null : new PrintStream(stream)));
	}
	
	/**
	 * Sets the error output message printstream.
	 *
	 * @param stream The printstream to use for error output.
	 */
	public void setMessageErrorStream(PrintStream stream) {
		MESSAGE_ERROR_STREAM = stream;
	}
	
	/**
	 * Sets the error output message outputstream. Wraps the stream given in a PrintStream
	 *
	 * @param stream The outputstream to use for error output.
	 */
	public void setMessageErrorStream(OutputStream stream) {
		setMessageErrorStream((stream == null ? null : new PrintStream(stream)));
	}
	
	/**
	 * Determines if a normal message can be output given the output level
	 *
	 * @param level The level of the message
	 * @return If the message is to be output
	 */
	public boolean canOutput(OutputLevel level) {
		if(MESSAGE_OUTPUT_STREAM == null) {
			return false;
		}
		if(OUTPUT_LEVEL_THRESHOLD == NONE) {
			return false;
		}
		
		return OUTPUT_LEVEL_THRESHOLD == level || level == DEFAULT;
	}
	
	/**
	 * Determines if an error message can be output.
	 *
	 * @return If the message is to be output
	 */
	public boolean canOutputErr() {
		if(MESSAGE_ERROR_STREAM == null) {
			return false;
		}
		return OUTPUT_LEVEL_THRESHOLD != NONE;
	}
	// </editor-fold>
	
	// <editor-fold desc="Actual printing methods">
	
	/**
	 * Prints a line to the normal printstream. Only does so if level given is at or above
	 * threshold.
	 *
	 * @param level  The level this message is.
	 * @param output The message to output.
	 */
	public void normPrintln(OutputLevel level, String output) {
		if(canOutput(level)) {
			MESSAGE_OUTPUT_STREAM.println(output);
		}
	}
	
	/**
	 * Prints a message to the normal printstream. Only does so if level given is at or above
	 * threshold.
	 *
	 * @param level  The level this message is.
	 * @param output The message to output.
	 */
	public void normPrint(OutputLevel level, String output) {
		if(canOutput(level)) {
			MESSAGE_OUTPUT_STREAM.print(output);
		}
	}
	
	/**
	 * Prints to the error print stream as a line. Ignores threshold unless it is {@link
	 * OutputLevel#NONE}
	 *
	 * @param output The string to output.
	 */
	public void errorPrintln(String output) {
		if(canOutputErr()) {
			MESSAGE_ERROR_STREAM.println(output);
		}
	}
	
	/**
	 * Prints to the error print stream. Ignores threshold unless it is {@link OutputLevel#NONE}
	 *
	 * @param output The string to output.
	 */
	public void errorPrint(String output) {
		if(canOutputErr()) {
			MESSAGE_ERROR_STREAM.print(output);
		}
	}
	// </editor-fold>
}
