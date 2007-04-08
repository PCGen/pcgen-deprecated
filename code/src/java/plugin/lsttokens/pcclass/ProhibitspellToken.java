package plugin.lsttokens.pcclass;

import java.util.Arrays;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken extends AbstractToken implements
		PCClassLstToken, PCClassClassLstToken
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		SpellProhibitor spellProb = new SpellProhibitor();

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken().toUpperCase();

			if (PreParserFactory.isPreReqString(aString))
			{
				try
				{
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					spellProb.addPreReq(factory.parse(aString));
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				final StringTokenizer elements =
						new StringTokenizer(aString, ".", false);
				final String aType = elements.nextToken();

				for (ProhibitedSpellType type : ProhibitedSpellType.values())
				{
					if (type.toString().equalsIgnoreCase(aType))
					{
						spellProb.setType(type);
						while (elements.hasMoreTokens())
						{
							String aValue = elements.nextToken();
							if (type.equals(ProhibitedSpellType.ALIGNMENT)
								&& (!aValue.equals("GOOD"))
								&& (!aValue.equals("EVIL"))
								&& (!aValue.equals("LAWFUL"))
								&& (!aValue.equals("CHAOTIC")))
							{
								Logging
									.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
										+ aValue + "'");
							}
							else
							{
								if (type.equals(ProhibitedSpellType.SPELL))
								{
									for (String spell : aValue.split(","))
									{
										spellProb.addValue(spell);
									}
								}
								else
								{
									spellProb.addValue(aValue);
								}
							}
						}
					}
				}
				if (spellProb.getType() == null)
				{
					Logging.errorPrint("Illegal PROHIBITSPELL subtag '"
						+ aString + "'");
				}
			}
		}
		pcclass.setProhibitSpell(spellProb);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		SpellProhibitor sp = subParse(context, pcc, value);
		if (sp == null)
		{
			return false;
		}
		context.graph.linkObjectIntoGraph(getTokenName(), pcc, sp);
		return true;
	}

	public SpellProhibitor subParse(LoadContext context, PCClass pcc,
		String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		SpellProhibitor spellProb = new SpellProhibitor();

		String token = tok.nextToken();

		while (true)
		{
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}

			StringTokenizer elements =
					new StringTokenizer(token, Constants.DOT);
			ProhibitedSpellType type;
			try
			{
				type = ProhibitedSpellType.valueOf(elements.nextToken());
				spellProb.setType(type);
			}
			catch (IllegalArgumentException e)
			{
				Logging
					.errorPrint(getTokenName()
						+ " encountered an invalid Prohibited Spell Type: "
						+ value);
				Logging.errorPrint("  Legal values are: "
					+ StringUtil.join(Arrays.asList(ProhibitedSpellType
						.values()), ", "));
				return null;
			}
			while (elements.hasMoreTokens())
			{
				String aValue = elements.nextToken();
				if (type.equals(ProhibitedSpellType.ALIGNMENT)
					&& (!aValue.equals("GOOD")) && (!aValue.equals("EVIL"))
					&& (!aValue.equals("LAWFUL"))
					&& (!aValue.equals("CHAOTIC")))
				{
					Logging
						.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
							+ aValue + "'");
				}
				else
				{
					if (type.equals(ProhibitedSpellType.SPELL))
					{
						for (String spell : aValue.split(","))
						{
							spellProb.addValue(spell);
						}
					}
					else
					{
						spellProb.addValue(aValue);
					}
				}
			}

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return spellProb;
			}
			token = tok.nextToken();
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
			}
			spellProb.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		return spellProb;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
