package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.CDOMSpecialAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

public class SabLst extends AbstractToken implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	private static final Class<CDOMSpecialAbility> SA_CLASS = CDOMSpecialAbility.class;

	@Override
	public String getTokenName()
	{
		return "SAB";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Skill)
		{
			Logging.errorPrint("SA not supported in Skills");
			return false;
		}
		return parseSpecialAbility(obj, value, anInt);
	}

	/**
	 * This method sets the special abilities granted by this [object].
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public boolean parseSpecialAbility(PObject obj, String value, int level)
	{
		if (value.startsWith(".CLEAR."))
		{
			String saName = value.substring(7);
			if (saName.indexOf("|") != -1)
			{
				Logging
					.errorPrint("Cannot .CLEAR. an SAB with a | in the token: "
						+ value);
				return false;
			}
			obj.removeSAB(saName, level);
			return true;
		}
		StringTokenizer tok = new StringTokenizer(value, "|");

		String token = tok.nextToken();

		if (".CLEAR".equals(token))
		{
			obj.clearSABList(level);
			if (!tok.hasMoreTokens())
			{
				return true;
			}
			token = tok.nextToken();
		}

		StringBuffer saName = new StringBuffer();
		saName.append(token);
		SpecialAbility sa = new SpecialAbility();

		boolean isPre = false;
		boolean first = false;

		while (tok.hasMoreTokens())
		{
			String argument = tok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(argument))
			{
				isPre = true;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(argument);
					if (obj instanceof PCClass
						&& "var".equals(prereq.getKind()))
					{
						prereq.setSubKey("CLASS:" + obj.getKeyName());
					}
					sa.addPreReq(prereq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
					return false;
				}
			}
			else if (token.startsWith(".CLEAR"))
			{
				Logging.errorPrint("Embedded .CLEAR in " + getTokenName()
					+ " is not supported: " + value);
				return false;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
						+ value);
					Logging
						.errorPrint("  PRExxx must be at the END of the Token");
					return false;
				}
				if (!first)
				{
					saName.append("|");
				}
				saName.append(argument);
			}
			first = false;
		}

		sa.setName(saName.toString());

		if (level >= 0)
		{
			try
			{
				sa.addPreReq(PreParserFactory.createLevelPrereq(obj, level));
			}
			catch (PersistenceLayerException notUsed)
			{
				Logging.errorPrint("Failed to assign level prerequisite.",
					notUsed);
			}
		}
		if (obj instanceof PCClass)
		{
			sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		}

		Globals.addToSASet(sa);
		obj.addSAB(sa, level);
		return true;
	}
	
	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
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
			context.getGraphContext().grant(getTokenName(), obj, sa);
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
				context.getGraphContext().grant(getTokenName(), obj, sa);
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
		context.getGraphContext().grant(getTokenName(), obj, sa);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<CDOMSpecialAbility> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, SA_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		List<String> list = new ArrayList<String>(added.size() + 1);
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		for (LSTWriteable lw : added)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(lw.getLSTformat());
			CDOMSpecialAbility ab = (CDOMSpecialAbility) lw;
			if (ab.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, ab
					.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
