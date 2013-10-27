package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMFollower;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with USEMASTERSKILL Token
 */
public class UsemasterskillToken implements CompanionModLstToken,
		CDOMPrimaryToken<CDOMFollower>
{

	public String getTokenName()
	{
		return "USEMASTERSKILL";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setUseMasterSkill(value.startsWith("Y"));
		return true;
	}

	public boolean parse(LoadContext context, CDOMFollower adj, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
							+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(adj, ObjectKey.USEMASTERSKILL, set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMFollower adj)
	{
		Boolean mult = context.getObjectContext().getObject(adj,
				ObjectKey.USEMASTERSKILL);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.booleanValue() ? "YES" : "NO" };
	}

	public Class<CDOMFollower> getTokenClass()
	{
		return CDOMFollower.class;
	}

}
