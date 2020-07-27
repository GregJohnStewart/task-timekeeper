package com.gjs.taskTimekeeper.baseCode.core.timeParser;

import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle Time parsing. Not instantiable, use the static classes.
 */
@Slf4j
public final class TimeParser {
	
	private TimeParser() {
		// prevent instantiation
	}
	
	protected enum DateTimeParsedType {
		DATETIME,
		DATE,
		TIME
	}
	
	/**
	 * The default date/time formatter to use in parsing and formatting
	 */
	private static DateTimeFormatter DEFAULT_OUTPUT_FORMATTER =
		DateTimeFormatter.ofPattern("d/M h:mm a y");
	/**
	 * The list of supported formatters
	 */
	private static final Map<DateTimeFormatter, DateTimeParsedType> FORMATTERS = new HashMap<>();
	/**
	 * A map of special date/time strings and the methods to get them, for processing those special
	 * datetime strings
	 */
	private static final Map<String, Method> TIME_VALUES = new HashMap<>();
	/**
	 * The default start of a week
	 */
	private static final DayOfWeek WEEK_START = DayOfWeek.MONDAY;
	/**
	 * List of acceptable formats for use with help outputs *
	 */
	private static final List<String> ACCEPTABLE_FORMATS_FOR_HELP = new ArrayList<>();
	
	/**
	 * Wrapper for {@link #getStartOfThisWeek()}. Gets that value and subtracts one week.
	 *
	 * @return {@link #getStartOfThisWeek()} minus one week.
	 */
	public static LocalDateTime getStartOfLastWeek() {
		return getStartOfThisWeek().minusWeeks(1);
	}
	
	/**
	 * Wrapper for {@link #getStartOfThisWeek()}. Gets that value and adds one week.
	 *
	 * @return {@link #getStartOfThisWeek()} plus one week.
	 */
	public static LocalDateTime getStartOfNextWeek() {
		return getStartOfThisWeek().plusWeeks(1);
	}
	
	/**
	 * Gets a datetime that is at midnight of the start of the current week. Day of week determined
	 * by {@link #WEEK_START}.
	 * https://stackoverflow.com/questions/28450720/get-date-of-first-day-of-week-based-on-localdate-now-in-java-8
	 *
	 * @return A datetime that is at midnight of the start of the current week.
	 */
	public static LocalDateTime getStartOfThisWeek() {
		return LocalDate.now().with(WEEK_START).atStartOfDay();
	}
	
	static {
		FORMATTERS.put(DateTimeFormatter.ISO_DATE_TIME, DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ISO_DATE, DateTimeParsedType.DATE);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeParsedType.DATE);
		FORMATTERS.put(DateTimeFormatter.ISO_TIME, DateTimeParsedType.TIME);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeParsedType.TIME);
		ACCEPTABLE_FORMATS_FOR_HELP.add("ISO Date/Times");
		
		FORMATTERS.put(DateTimeFormatter.ofPattern("d/M h:m a y"), DateTimeParsedType.DATETIME);
		ACCEPTABLE_FORMATS_FOR_HELP.add("d/M h:m a y");
		FORMATTERS.put(DateTimeFormatter.ofPattern("d/M H:m y"), DateTimeParsedType.DATETIME);
		ACCEPTABLE_FORMATS_FOR_HELP.add("d/M H:m y");
		FORMATTERS.put(DateTimeFormatter.ofPattern("d/M y"), DateTimeParsedType.DATE);
		ACCEPTABLE_FORMATS_FOR_HELP.add("d/M y");
		FORMATTERS.put(DateTimeFormatter.ofPattern("h:m a"), DateTimeParsedType.TIME);
		ACCEPTABLE_FORMATS_FOR_HELP.add("h:m a");
		FORMATTERS.put(DateTimeFormatter.ofPattern("H:m"), DateTimeParsedType.TIME);
		ACCEPTABLE_FORMATS_FOR_HELP.add("H:m");
		
		try {
			TIME_VALUES.put("NOW", LocalDateTime.class.getMethod("now"));
			ACCEPTABLE_FORMATS_FOR_HELP.add("NOW");
			TIME_VALUES.put("LAST_WEEK", TimeParser.class.getMethod("getStartOfLastWeek"));
			ACCEPTABLE_FORMATS_FOR_HELP.add("LAST_WEEK");
			TIME_VALUES.put("THIS_WEEK", TimeParser.class.getMethod("getStartOfThisWeek"));
			ACCEPTABLE_FORMATS_FOR_HELP.add("THIS_WEEK");
			TIME_VALUES.put("NEXT_WEEK", TimeParser.class.getMethod("getStartOfNextWeek"));
			ACCEPTABLE_FORMATS_FOR_HELP.add("NEXT_WEEK");
			TIME_VALUES.put("LAST WEEK", TimeParser.class.getMethod("getStartOfLastWeek"));
			TIME_VALUES.put("THIS WEEK", TimeParser.class.getMethod("getStartOfThisWeek"));
			TIME_VALUES.put("NEXT WEEK", TimeParser.class.getMethod("getStartOfNextWeek"));
			TIME_VALUES.put("LASTWEEK", TimeParser.class.getMethod("getStartOfLastWeek"));
			TIME_VALUES.put("THISWEEK", TimeParser.class.getMethod("getStartOfThisWeek"));
			TIME_VALUES.put("NEXTWEEK", TimeParser.class.getMethod("getStartOfNextWeek"));
		} catch(NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses the String into a LocalDateTime
	 *
	 * @param input The String to parse into a datetime
	 * @return The local datetime parsed from the string. Null if string given could not be parsed.
	 */
	public static LocalDateTime parse(String input) {
		log.debug("Parsing LocalDateTime from: \"{}\"", input);
		String washedInput = washTimeVal(input);
		log.debug("Washed time string: \"{}\"", washedInput);
		
		for(Map.Entry<String, Method> entry : TIME_VALUES.entrySet()) {
			if(entry.getKey().equalsIgnoreCase(washedInput)) {
				try {
					return (LocalDateTime)entry.getValue().invoke(null);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		for(Map.Entry<DateTimeFormatter, DateTimeParsedType> formatter : FORMATTERS.entrySet()) {
			try {
				TemporalAccessor parsed = formatter.getKey().parse(washedInput);
				
				switch(formatter.getValue()) {
				case DATETIME:
					return LocalDateTime.from(parsed);
				case DATE:
					// TODO:: figure out how to add year to the parsed, for entering simpler
					// dates
					return LocalDate.from(parsed).atStartOfDay();
				case TIME:
					return LocalTime.from(parsed).atDate(LocalDate.now());
				}
			} catch(DateTimeException e) {
				// go to the next datetime format to see if it works
				continue;
			}
		}
		
		return null;
	}
	
	private static String washTimeVal(String string) {
		return string.trim();
	}
	
	/**
	 * Outputs the help information to the outputter given.
	 *
	 * @param outputter The outputter to use to output the help.
	 */
	public static void outputHelp(Outputter outputter) {
		log.info("Listing available date/time formats.");
		outputter.normPrintln(OutputLevel.DEFAULT, "Date/Time formats available for use:");
		for(String curFormat : ACCEPTABLE_FORMATS_FOR_HELP) {
			outputter.normPrintln(OutputLevel.DEFAULT, "\t" + curFormat);
		}
		outputter.normPrintln(
			OutputLevel.DEFAULT,
			"More info on using these patterns can be found here: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html"
		);
	}
	
	/**
	 * Outputs the help information to the {@link Outputter#getDefaultOutputter() default outputter}
	 */
	public static void outputHelp() {
		outputHelp(Outputter.getDefaultOutputter());
	}
	
	/**
	 * Sets the default output formatter, and adds the format to the list of formatters for input.
	 *
	 * @param newFormatter The new formatter to use.
	 * @throws IllegalArgumentException if the formatter was not valid for parsing local datetimes.
	 */
	private static void setDefaultOutputFormatter(DateTimeFormatter newFormatter)
		throws IllegalArgumentException {
		try {
			LocalDateTime.from(newFormatter.parse(newFormatter.format(LocalDateTime.now())));
		} catch(Exception e) {
			throw new IllegalArgumentException("DateTime formatter given was invalid.", e);
		}
		
		DEFAULT_OUTPUT_FORMATTER = newFormatter;
		FORMATTERS.put(newFormatter, DateTimeParsedType.DATETIME);
	}
	
	/**
	 * Sets the default output formatter, and adds the format to the list of formatters for input.
	 *
	 * @param newFormat The format to set
	 * @throws IllegalArgumentException if the format was not valid for parsing local datetimes.
	 */
	public static void setDefaultOutputFormatter(String newFormat) throws IllegalArgumentException {
		setDefaultOutputFormatter(DateTimeFormatter.ofPattern(newFormat));
	}
	
	/**
	 * Gets the datetime as a string described in the default output formatter.
	 *
	 * @param dateTime The datetime to format
	 * @return The String described by the default output formatter. If dateTime null, return empty
	 * 	string.
	 */
	public static String toOutputString(LocalDateTime dateTime) {
		if(dateTime == null) {
			return "";
		}
		return DEFAULT_OUTPUT_FORMATTER.format(dateTime);
	}
	
	/**
	 * TODO:: label? add seconds. Optionally?
	 * Gets the duration (in hours and minutes ("h:m")) the duration given describes. If longer than
	 * a day, adds the number of hours in the day(s) to the hour count.
	 *
	 * @param duration The duration to work off of.
	 * @return A String representation of the duration given.
	 */
	public static String toDurationString(Duration duration) {
		// original JDK 11 compliant. Backported to JDK 8 for compatibility reasons
		// return (duration.toDaysPart() != 0 ? duration.toDaysPart() * 24 : 0) +
		// duration.toHoursPart() + ":" + duration.toMinutesPart();
		
		Duration left = duration;
		int hours = 0;
		int minutes = 0;
		
		if(left.toDays() != 0) {
			hours += left.toDays() * 24;
			left = left.minusDays(left.toDays());
		}
		
		hours += left.toHours();
		left = left.minusHours(left.toHours());
		
		minutes += left.toMinutes();
		
		return hours + ":" + minutes;
	}
	
	/**
	 * TODO:: think through, test
	 *
	 * @param duration
	 * @return
	 */
	public static String toDurationStringExact(Duration duration) {
		// original JDK 11 compliant. Backported to JDK 8 for compatibility reasons
		// return (duration.toDaysPart() != 0 ? duration.toDaysPart() * 24 : 0) +
		// duration.toHoursPart() + ":" + duration.toMinutesPart();
		
		Duration left = duration;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		
		if(left.toDays() != 0) {
			hours += left.toDays() * 24;
			left = left.minusDays(left.toDays());
		}
		
		hours += left.toHours();
		left = left.minusHours(left.toHours());
		
		minutes += left.toMinutes();
		left = left.minusMinutes(left.toMinutes());
		
		seconds += left.getSeconds();
		
		return hours + ":" + minutes + ":" + seconds;
	}
}
