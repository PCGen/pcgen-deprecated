package plugin.lstcompatibility.global;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CDOMSpecialAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class SA514Lst extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof CDOMPCClass)
		{
			return false;
		}
		return parseSpecialAbility(context, obj, value);
	}

	/**
	 * This method sets the special abilities granted by this [object]. For
	 * efficiency, avoid calling this method except from I/O routines.
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public boolean parseSpecialAbility(LoadContext context, CDOMObject obj,
			String aString)
	{
		if (isEmpty(aString) || hasIllegalSeparator('|', aString))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(aString, Constants.PIPE);

		String firstToken = tok.nextToken();
		if (firstToken.startsWith("PRE") || firstToken.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
					+ getTokenName());
			return false;
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.getGraphContext().removeAll(getTokenName(), obj);
			if (!tok.hasMoreTokens())
			{
				return true;
			}
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint("SA tag confused by redundant '.CLEAR'"
					+ aString);
			return false;
		}

		CDOMSpecialAbility sa = new CDOMSpecialAbility(firstToken);

		if (!tok.hasMoreTokens())
		{
			context.getObjectContext().give(getTokenName(), obj, sa);
			return true;
		}

		StringBuilder saName = new StringBuilder();
		saName.append(firstToken);

		String token = tok.nextToken();
		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint("SA tag confused by '.CLEAR' as a "
						+ "middle token: " + aString);
				return false;
			}
			else if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			else
			{
				saName.append(Constants.PIPE).append(token);
				// sa.addVariable(FormulaFactory.getFormulaFor(token));
			}

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				// CONSIDER This is a HACK and not the long term strategy of SA:
				sa.setName(saName.toString());
				context.getObjectContext().give(getTokenName(), obj, sa);
				return true;
			}
			token = tok.nextToken();
		}
		// CONSIDER This is a HACK and not the long term strategy of SA:
		sa.setName(saName.toString());

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put Abilities after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			/*
			 * The following subkey is required in order to give context to the
			 * variables as they are calculated (make the context the current
			 * class, so that items like Class Level can be correctly
			 * calculated).
			 */
			if (obj instanceof CDOMPCClass && "var".equals(prereq.getKind()))
			{
				prereq.setSubKey("CLASS:" + obj.getKeyName());
			}
			sa.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		context.getObjectContext().give(getTokenName(), obj, sa);
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
