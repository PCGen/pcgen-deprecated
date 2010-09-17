package plugin.lsttokens.pcclass.level;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DONOTADD Token
 */
public class DonotaddToken extends AbstractToken implements
		CDOMPrimaryToken<PCClassLevel>
{
	@Override
	public String getTokenName()
	{
		return "DONOTADD";
	}

	public boolean parse(LoadContext context, PCClassLevel level, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		while (pipeTok.hasMoreTokens())
		{
			String tokText = pipeTok.nextToken();
			if ("HITDIE".equals(tokText))
			{
				context.getObjectContext().put(level, ObjectKey.DONTADD_HITDIE, Boolean.TRUE);
			}
			else if ("SKILLPOINTS".equals(tokText))
			{
				context.getObjectContext().put(level, ObjectKey.DONTADD_SKILLPOINTS, Boolean.TRUE);
			}
			else
			{
				Logging.errorPrint(getTokenName() + " encountered an invalid 'Do Not Add' type: " + value +
					". Legal values are: HITDIE, SKILLPOINTS");
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClassLevel level)
	{
		Boolean increaseHitDice = context.getObjectContext().getObject(level, ObjectKey.DONTADD_HITDIE);
		Boolean increaseSkills  = context.getObjectContext().getObject(level, ObjectKey.DONTADD_SKILLPOINTS);

		if ((increaseHitDice == null) && (increaseSkills == null))
		{
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if (increaseHitDice != null)
		{
			sb.append("HITDIE");
		}

		if (increaseSkills != null)
		{
			if (increaseHitDice != null)
			{
				sb.append(Constants.PIPE);
			}
			sb.append("SKILLPOINTS");
		}

		return new String[] { sb.toString() };
	}

	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}
}
