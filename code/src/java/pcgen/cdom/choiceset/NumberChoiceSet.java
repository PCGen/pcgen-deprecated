package pcgen.cdom.choiceset;

import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class NumberChoiceSet implements PrimitiveChoiceSet<Integer>
{

	private final int minimum;
	private final int maximum;
	private boolean showZero = true;
	private boolean showSign = true;
	private boolean multiple = false;
	private int increment = 1;

	public NumberChoiceSet(int min, int max)
	{
		minimum = min;
		maximum = max;
	}

	public Class<Integer> getReferenceClass()
	{
		return Integer.class;
	}

	public Set<Integer> getSet(CharacterDataStore pc)
	{
		/*
		 * TODO Need to resolve Numbers
		 */
		return null;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder().append("MIN=").append(minimum)
				.append("|MAX=").append(maximum);
		if (!showZero)
		{
			sb.append("|SKIPZERO");
		}
		if (!showSign)
		{
			sb.append("|NOSIGN");
		}
		if (multiple)
		{
			sb.append("|MULTIPLE");
		}
		if (increment != 1)
		{
			sb.append("|INCREMENT=" + increment);
		}
		return sb.toString();
	}

	public Class<Integer> getChoiceClass()
	{
		return Integer.class;
	}

	public void setShowZero(boolean b)
	{
		showZero = b;
	}

	public void setShowSign(boolean b)
	{
		showSign = b;
	}

	public void setMultiple(boolean b)
	{
		multiple = b;
	}

	public void setIncrement(int i)
	{
		if (i < 1)
		{
			throw new IllegalArgumentException("Increment must be >= 1");
		}
		increment = i;
	}
}
