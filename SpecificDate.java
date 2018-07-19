import java.util.Calendar;
import java.io.Serializable;

/**
* A class representing a date with day precision.
*/
public class SpecificDate implements Serializable
{
	private int day, month, year;

	/**
	* Constructs the current date.
	*/
	public SpecificDate()
	{
		Calendar instance = Calendar.getInstance();
		day = instance.get(Calendar.DAY_OF_MONTH);
		month = instance.get(Calendar.MONTH) + 1;
		year = instance.get(Calendar.YEAR);
	}

	/**
	* Checks if two specific dates refer to the same point in time (same day, month and year).
	* @param obj a SpecificDate object to compare with this one
	* @return true if the objects refer to the same date, false otherwise
	*/
	@Override
	public boolean equals(Object obj)
	{
		SpecificDate other = (SpecificDate) obj;
		return (day == other.day && month == other.month && year == other.year);
	}

	/**
	* The String representation of a specific date.
	* @return a string of the form "day: D, month: M, year: Y"
	*/
	@Override
	public String toString()
	{
		return String.format("day: " + day + ", month: " + month + ", year: " + year);
	}

	/**
	* Calculates the hash code value for the object.
	* @return the hash code
	*/
	@Override
	public int hashCode()
	{
		return (day + month + year);
	}
}