package com.rach.archexemplar.utility;


import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author rachaelcolley
 */

public class ConvertTime {

	private final static String GMT_TIMEZONE = "Etc/GMT";
	private final static String AUST_CITIES[] = {"adelaide","brisbane","canberra","darwin","hobart","melbourne","perth","sydney"};
	private final static String AUST_TIMEZONES[] = {"Australia/Adelaide","Australia/Brisbane","Australia/Canberra","Australia/Darwin","Australia/Hobart","Australia/Melbourne","Australia/Perth","Australia/Sydney"};
	private final static String JSON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private final static String AM = "AM";
	private final static String PM = "PM";
	private final static String NOON = "Noon";
	private final static String MIDNIGHT = "Midnight";
	private final static String CURRENT_DATETIME_FORMAT = "HH:mm";

	/**
	 * Gets the current system time.
	 * @return String in yyyy-MM-dd HH:mm:ss pattern.
	 */
	public static String getCurrentSystemTime() {
		DateTime dt = new DateTime();
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(CURRENT_DATETIME_FORMAT);
		return dt.toString(dateTimeFormatter);
	}

	/**
	 * Gets the current time for an Australian city.
	 * @param cityName The name of the city to get the time for.
	 * @return String The Australian city time in HH:mm pattern.
	 */
	public static String getTimeForCity(String cityName) {
		DateTime currentDateTime = new DateTime();
		int cityPos = Arrays.binarySearch(AUST_CITIES, cityName.toLowerCase());
		String toTimeZoneId = AUST_TIMEZONES[cityPos];
		DateTimeZone toDateTimeZone = DateTimeZone.forID(toTimeZoneId);
		DateTime toDateTime = currentDateTime.toDateTime(toDateTimeZone);
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(CURRENT_DATETIME_FORMAT);
		return toDateTime.toString(dateTimeFormatter);
	}

	/**
	 * Converts a UTC date and time string to an Australian city date and time.
	 * @param JSONDateTime Date and time string in the following format yyyy-MM-dd HH:mm:s
	 * @param cityName The name of the city to convert the time to.
	 * @return DateTime instance containing converted time.
	 */
	private static DateTime convertTime(String JSONDateTime, String cityName) {
		int cityPos = Arrays.binarySearch(AUST_CITIES, cityName.toLowerCase());
		String toTimeZoneId = AUST_TIMEZONES[cityPos];
		DateTimeZone gmtDateTimeZone = DateTimeZone.forID(GMT_TIMEZONE);
		DateTimeZone toDateTimeZone = DateTimeZone.forID(toTimeZoneId);
		DateTimeFormatter gmtDateTimeFormatter = DateTimeFormat.forPattern(JSON_DATETIME_FORMAT).withZone(gmtDateTimeZone);
		DateTime gmtDateTime = gmtDateTimeFormatter.parseDateTime(JSONDateTime); 
		DateTime toDateTime = gmtDateTime.toDateTime(toDateTimeZone);
		return toDateTime;
	}
	
	/**
	 * Converts a (UTC) Unix Timestamp to an Australian city date and time.
	 * @param timeStamp Unix Timestamp String
	 * @param cityName The name of the city to convert the time to.
	 * @return DateTime instance containing converted time.
	 */
	private static DateTime convertTime(int timeStamp, String cityName) {
		Long utcTimeStamp = (long) timeStamp;
		int cityPos = Arrays.binarySearch(AUST_CITIES, cityName.toLowerCase());
		String toTimeZoneId = AUST_TIMEZONES[cityPos];
		DateTimeZone gmtDateTimeZone = DateTimeZone.forID(GMT_TIMEZONE);
		DateTimeZone toDateTimeZone = DateTimeZone.forID(toTimeZoneId);
		DateTime ldt = new DateTime(utcTimeStamp*1000,gmtDateTimeZone);
		DateTime toDateTime = ldt.toDateTime(toDateTimeZone);
		return toDateTime;
	}

	/**
	 * Converts a UTC date and time string to an Australian city date String.
	 * @param JSONDateTime Date and time string in the following format yyyy-MM-dd HH:mm:s
	 * @param cityName The name of the city to convert the date to.
	 * @return String The Australian city date.
	 */
	public static String makeDateStringForCity(String JSONDateTime, String cityName) {
		DateTime dateTime = convertTime(JSONDateTime, cityName);
		String day = dateTime.property(DateTimeFieldType.dayOfWeek()).getAsShortText();
		String dayOfMonth = dateTime.property(DateTimeFieldType.dayOfMonth()).getAsShortText();
		String month = dateTime.property(DateTimeFieldType.monthOfYear()).getAsShortText();
		String year = dateTime.property(DateTimeFieldType.year()).getAsShortText();
		String currentDate = day + " " + dayOfMonth + " " + month + " " + year;		
		return currentDate;
	}
	
	public static String makeDateStringForCity(int timeStamp, String cityName) {
		DateTime dateTime = convertTime(timeStamp, cityName);
		String day = dateTime.property(DateTimeFieldType.dayOfWeek()).getAsShortText();
		String dayOfMonth = dateTime.property(DateTimeFieldType.dayOfMonth()).getAsShortText();
		String month = dateTime.property(DateTimeFieldType.monthOfYear()).getAsShortText();
		String year = dateTime.property(DateTimeFieldType.year()).getAsShortText();
		String currentDate = day + " " + dayOfMonth + " " + month + " " + year;		
		return currentDate;
	}

	/**
	 * Gets the converted clock hour from a UTC date and time string.
	 * @param JSONDateTime JSONDateTime Date and time string in the following format yyyy-MM-dd HH:mm:s
	 * @param cityName The name of the city to get the clock hour for.
	 * @return String String containing the clock hour for the city. 
	 */
	public static String makeClockHourForCity(String JSONDateTime, String cityName) {
		DateTime dateTime = convertTime(JSONDateTime, cityName);
		String clockHour = null;
		int clHour = dateTime.getHourOfDay();

		if (clHour == 12) {
			clockHour = NOON;
		} else if (clHour == 00) {
			clockHour = MIDNIGHT;
		} else {
			clockHour = dateTime.property(DateTimeFieldType.clockhourOfHalfday()).getAsShortText();
		}

		String ampm = dateTime.property(DateTimeFieldType.halfdayOfDay()).getAsShortText().toLowerCase(Locale.ENGLISH);

		if (ampm.equals(AM)) {
			ampm = AM;
		}
		if (ampm.equals(PM)) {
			ampm = PM;
		}

		return clockHour + " " + ampm;
	}

}

