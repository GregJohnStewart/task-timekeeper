package com.gjs.taskTimekeeper.backend.timeParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

public class TimeParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeParser.class);

	protected enum DateTimeParsedType{
		DATETIME,
		DATE,
		TIME
	}

	private static DateTimeFormatter DEFAULT_OUTPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; //DateTimeFormatter.ofPattern("d/L h:m a");
	private static final Map<DateTimeFormatter, DateTimeParsedType> FORMATTERS = new HashMap<>();
	private static final Map<String, Method> TIME_VALUES = new HashMap<>();

	static {
		FORMATTERS.put(DateTimeFormatter.ISO_DATE_TIME, DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ISO_DATE, DateTimeParsedType.DATE);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeParsedType.DATE);
		FORMATTERS.put(DateTimeFormatter.ISO_TIME, DateTimeParsedType.TIME);
		FORMATTERS.put(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeParsedType.TIME);

		FORMATTERS.put(DateTimeFormatter.ofPattern("d/L h:m a"), DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ofPattern("d/L H:m"), DateTimeParsedType.DATETIME);
		FORMATTERS.put(DateTimeFormatter.ofPattern("d/L"), DateTimeParsedType.DATE);
		FORMATTERS.put(DateTimeFormatter.ofPattern("h:m a"), DateTimeParsedType.TIME);
		FORMATTERS.put(DateTimeFormatter.ofPattern("H:m"), DateTimeParsedType.TIME);

		try {
			TIME_VALUES.put("NOW", LocalDateTime.class.getMethod("now"));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Parses the String into a LocalDateTime
	 * @param input The String to parse into a datetime
	 * @return The local datetime parsed from the string. Null if string given could not be parsed.
	 */
	public static LocalDateTime parse(String input) {
		LOGGER.debug("Parsing LocalDateTime from: \"{}\"", input);
		String washedInput = washTimeVal(input);
		LOGGER.debug("Washed time string: \"{}\"", washedInput);

		for (Map.Entry<String, Method> entry : TIME_VALUES.entrySet()) {
			if(entry.getKey().equalsIgnoreCase(washedInput)){
				try {
					return (LocalDateTime)entry.getValue().invoke(null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		for(Map.Entry<DateTimeFormatter, DateTimeParsedType> formatter : FORMATTERS.entrySet()){
			try{
				TemporalAccessor parsed = formatter.getKey().parse(washedInput);

				switch (formatter.getValue()){
					case DATETIME:
						return LocalDateTime.from(parsed);
					case DATE:
						return LocalDate.from(parsed).atStartOfDay();
					case TIME:
						return LocalTime.from(parsed).atDate(LocalDate.now());
				}
			}catch (DateTimeException e){
				continue;
			}
		}

		return null;
	}

	private static String washTimeVal(String string){
		return string.trim();
	}

	public static void outputHelp(){
		LOGGER.info("Listing available date/time formats.");
		System.out.println("Date/Time formats available for use:");
		for(Map.Entry<DateTimeFormatter, DateTimeParsedType> formatter : FORMATTERS.entrySet()){
			//TODO:: this isn't helpful, but the best we got? https://stackoverflow.com/questions/28949179/get-original-pattern-string-given-a-jdk-8-datetimeformatter
			System.out.println("\t" + formatter.getKey());
		}
		for(Map.Entry<String, Method> entry : TIME_VALUES.entrySet()){
			System.out.println("\t" + entry.getKey());
		}
	}

	/**
	 * Sets the default output formatter, and adds the format to the list of formatters for input.
	 * @param newFormatter The new formatter to use.
	 */
	private static void setDefaultOutputFormatter(DateTimeFormatter newFormatter){
		//TODO:: verify is datetime formatter
		DEFAULT_OUTPUT_FORMATTER = newFormatter;
		FORMATTERS.put(newFormatter, DateTimeParsedType.DATETIME);
	}

	/**
	 * Sets the default output formatter, and adds the format to the list of formatters for input.
	 * @param newFormat The format to set
	 */
	public static void setDefaultOutputFormatter(String newFormat){
		setDefaultOutputFormatter(DateTimeFormatter.ofPattern(newFormat));
	}

	/**
	 * Gets the datetime as a string described in the default output formatter.
	 * @param dateTime The datetime to format
	 * @return The String described by the default output formatter. If dateTime null, return empty string.
	 */
	public static String toOutputString(LocalDateTime dateTime){
		if(dateTime == null){
			return "";
		}
		return DEFAULT_OUTPUT_FORMATTER.format(dateTime);
	}

	public static String toDurationString(Duration duration){
		return duration.toHoursPart() + ":" + duration.toMinutesPart();
	}
}
