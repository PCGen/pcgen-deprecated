package pcgen.cdom.helper;

import java.util.Set;

import pcgen.core.PlayerCharacter;

public class NumberChoiceSet implements PrimitiveChoiceSet<Integer>
{

	private final int minimum;
	private final int maximum;
	private final String choiceTitle;

	public NumberChoiceSet(int min, int max, String title)
	{
		minimum = min;
		maximum = max;
		choiceTitle = title;
	}

	public Class<Integer> getReferenceClass()
	{
		return Integer.class;
	}

	public Set<Integer> getSet(PlayerCharacter pc)
	{
		return null;
	}

	public String getLSTformat()
	{
		StringBuilder sb =
				new StringBuilder().append("MIN=").append(minimum).append(
					"|MAX=").append(maximum);
		if (choiceTitle != null)
		{
			sb.append("|TITLE=").append(choiceTitle);
		}
		return sb.toString();
	}

	public Class<Integer> getChoiceClass()
	{
		return Integer.class;
	}

}
