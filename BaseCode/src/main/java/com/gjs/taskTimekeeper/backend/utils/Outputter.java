package com.gjs.taskTimekeeper.backend.utils;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.gjs.taskTimekeeper.backend.utils.OutputLevel.DEFAULT;
import static com.gjs.taskTimekeeper.backend.utils.OutputLevel.NONE;

/**
 * Class to handle outputting action and other runtime information to a printstream.
 *
 * Meant to be used staticly, all instances that use this will be used shared streams.
 *
 * TODO:: move to non static class to use?
 */
public final class Outputter {
	private static PrintStream MESSAGE_OUTPUT_STREAM;
	private static PrintStream MESSAGE_ERROR_STREAM;
	private static OutputLevel OUTPUT_LEVEL_THRESHOLD = DEFAULT;

	private Outputter(){
		//prevent instantiation
	}

	static {
		resetStreamsToDefault();
	}

	//<editor-fold desc="Public methods">
	/**
	 * Resets the streams to their default printstreams. ({@link System#out} for normal, {@link System#err} for error.
	 */
	public static void resetStreamsToDefault(){
		MESSAGE_OUTPUT_STREAM = System.out;
		MESSAGE_ERROR_STREAM = System.err;
	}

	/**
	 * Sets the threshold to use when determining whether or not to output.
	 * @param level The level to set.
	 */
	public static void setOutputLevelThreshold(OutputLevel level){
		if(level == null){
			throw new IllegalArgumentException("Output level cannot be null");
		}
		OUTPUT_LEVEL_THRESHOLD = level;
	}

	/**
	 * Sets the normal output message printstream.
	 * @param stream The printstream to use for normal output.
	 */
	public static void setMessageOutputStream(PrintStream stream){
		MESSAGE_OUTPUT_STREAM = stream;
	}
	/**
	 * Sets the normal output message outputstream. Wraps the stream given in a PrintStream
	 * @param stream The outputstream to use for normal output.
	 */
	public static void setMessageOutputStream(OutputStream stream){
		setMessageOutputStream(new PrintStream(stream));
	}

	/**
	 * Sets the error output message printstream.
	 * @param stream The printstream to use for error output.
	 */
	public static void setMessageErrorStream(PrintStream stream){
		MESSAGE_ERROR_STREAM = stream;
	}/**
	 * Sets the error output message outputstream. Wraps the stream given in a PrintStream
	 * @param stream The outputstream to use for error output.
	 */
	public static void setMessageErrorStream(OutputStream stream){
		setMessageErrorStream(new PrintStream(stream));
	}

	/**
	 * Determines if a normal message can be output given the output level
	 * @param level The level of the message
	 * @return If the message is to be output
	 */
	public static boolean canOutput(OutputLevel level){
		if(MESSAGE_OUTPUT_STREAM == null){
			return false;
		}
		if(OUTPUT_LEVEL_THRESHOLD == NONE){
			return false;
		}

		return OUTPUT_LEVEL_THRESHOLD == level ||
			       level == DEFAULT;
	}

	/**
	 * Determines if an error message can be output.
	 * @return If the message is to be output
	 */
	public static boolean canOutputErr(){
		if(MESSAGE_OUTPUT_STREAM == null){
			return false;
		}
		return OUTPUT_LEVEL_THRESHOLD == NONE;
	}
	//</editor-fold>

	//<editor-fold desc="Actual printing methods">
	/**
	 * Prints a line to the normal printstream. Only does so if level given is at or above threshold.
	 * @param level The level this message is.
	 * @param output The message to output.
	 */
	public static void consolePrintln(OutputLevel level, String output){
		if(canOutput(level)) {
			MESSAGE_OUTPUT_STREAM.println(output);
		}
	}

	/**
	 * Prints a message to the normal printstream. Only does so if level given is at or above threshold.
	 * @param level The level this message is.
	 * @param output The message to output.
	 */
	public static void consolePrint(OutputLevel level, String output){
		if(canOutput(level)){
			MESSAGE_OUTPUT_STREAM.print(output);
		}
	}

	/**
	 * Prints to the error print stream as a line. Ignores threshold unless it is {@link OutputLevel#NONE}
	 * @param output The string to output.
	 */
	public static void consoleErrorPrintln(String output){
		if(canOutputErr()) {
			MESSAGE_ERROR_STREAM.println(output);
		}
	}

	/**
	 * Prints to the error print stream. Ignores threshold unless it is {@link OutputLevel#NONE}
	 * @param output The string to output.
	 */
	public static void consoleErrorPrint(String output){
		if(canOutputErr()) {
			MESSAGE_ERROR_STREAM.print(output);
		}
	}
	//</editor-fold>
}
