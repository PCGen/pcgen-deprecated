package pcgen.util.enumeration;

public enum Visibility
{

	NO("No"), // Does not show up either in the GUI or on the output sheet
	YES("Yes"), // Shows up both in the GUI and on the output sheet
	EXPORT("Export"), // Shows up on the output sheet, but not in the GUI
	DISPLAY("Display"), //  Shows up in the GUI, but not on the output sheet
	QUALIFY("Qualify"); // Shows up in a Customizer only when qualified

	private final String text;

	Visibility(String s)
	{
		text = s;
	}

	@Override
	public String toString()
	{
		return text;
	}

	/**
	 * Determine if this visibility can be seen in the supplied view level.
	 * 
	 * @param view The view level.
	 * @param isExporting Is the visibility being detemerined for an export function
	 * @return true if the visibility can be viewed, false if not.
	 */
	public boolean isVisibileTo(View view, boolean isExporting)
	{
		if (view == View.ALL)
		{
			return true;
		}
		if (view == View.HIDDEN)
		{
			if (this == Visibility.NO || this == Visibility.DISPLAY)
			{
				return true;
			}
		}
		else
		{
			if (this == Visibility.YES || this == Visibility.EXPORT)
			{
				return true;
			}
		}
		return false;
	}

	public CharSequence getLSTFormat()
	{
		return toString().toUpperCase();
	}
}
