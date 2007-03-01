package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(Skill skill, String value)
	{
		final String visType = value.toUpperCase();

		if (visType.startsWith("YES") || visType.startsWith("ALWAYS"))
		{
			skill.setVisibility(Visibility.YES);
		}
		else if (visType.startsWith("GUI"))
		{
			skill.setVisibility(Visibility.DISPLAY);
		}
		else if (visType.startsWith("EXPORT") || visType.startsWith("CSHEET"))
		{
			skill.setVisibility(Visibility.EXPORT);
		}
		else
		{
			skill.setVisibility(Visibility.YES);
			return false;
		}

		String[] elements = visType.split("\\|");

		if (elements.length > 1)
		{
			if (elements[1].equals("READONLY") && !visType.startsWith("EXPORT"))
			{
				skill.setReadOnly(true);
			}
			else
			{
				return false;
			}
		}
		return true;
	}
}
