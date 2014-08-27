package ch.makery.address.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Helper class for handling dates.
 */
public class CalendarUtil {

	/**
	 * Default date format in the form 2013-03-18.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Returns the given date as a well formatted string. The above defined date
	 * format is used.
	 * 
	 * @param calendar date to be returned as a string
	 * @return formatted string
	 */
	public static String format(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return DATE_FORMAT.format(calendar.getTime());
	}

	/**
	 * Converts a String in the format "yyyy-MM-dd" to a Calendar object.
	 * 
	 * Returns null if the String could not be converted.
	 * 
	 * @param dateString the date as String
	 * @return the calendar object or null if it could not be converted
	 */
	public static Calendar parse(String dateString) {
		Calendar result = Calendar.getInstance();
		try {
			result.setTime(DATE_FORMAT.parse(dateString));
			return result;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Checks the String whether it is a valid date.
	 * 
	 * @param dateString
	 * @return true if the String is a valid date
	 */
	public static boolean validString(String dateString) {
		try {
			DATE_FORMAT.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}
