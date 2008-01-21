package pcgen.persistence.lst;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.ReverseIntegerComparator;
import pcgen.base.util.TripleKeyMap;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class PCClassLevelLoader
{

	public static void parseLine(LoadContext context, PCClass pcclass,
		String lstLine, CampaignSourceEntry source, int level)
	{
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken().trim();
			int idxColon = colString.indexOf(':');
			if (idxColon == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
					+ colString + " in source: " + source);
				return;
			}
			else if (idxColon == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
					+ colString + " in source: " + source);
				return;
			}
			String key = colString.substring(0, idxColon);
			String value =
					(idxColon == colString.length() - 1) ? null : colString
						.substring(idxColon + 1);

			PCClassUniversalLstToken univtoken =
					TokenStore.inst().getToken(PCClassUniversalLstToken.class,
						key);
			PCClassLevelLstToken levelToken =
					TokenStore.inst().getToken(PCClassLevelLstToken.class, key);

			if (levelToken == null)
			{
				if (processLevelCompatible(context, pcclass, key, value,
					level))
				{
					context.commit();
				}
				else
				{
					context.decommit();
					PCClassLoader.processUniversalToken(context, pcclass
						.getClassLevel(level), key, value, source, univtoken);
				}
			}
			else
			{
				LstUtils.deprecationCheck(levelToken, pcclass, value);
				if (levelToken.parse(context, pcclass, value, level))
				{
					context.commit();
				}
				else
				{
					context.decommit();
					Logging.markParseMessages();
					if (processLevelCompatible(context, pcclass, key, value,
						level))
					{
						context.commit();
					}
					else
					{
						context.decommit();
						Logging.rewindParseMessages();
						Logging.replayParsedMessages();
						Logging.errorPrint("Error parsing token " + key
							+ " in pcclass " + pcclass.getDisplayName()
							+ " level " + level + ' ' + source.getURI()
							+ ": \"" + value + "\"");
					}
					Logging.clearParseMessages();
				}
			}
		}
	}

	private static final ReverseIntegerComparator REVERSE =
			new ReverseIntegerComparator();

	private static boolean processLevelCompatible(LoadContext context,
		PCClass pcclass, String key, String value, int level)
	{
		Collection<PCClassLevelLstCompatibilityToken> tokens =
				TokenStore.inst().getCompatibilityToken(
					PCClassLevelLstCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, PCClassLevelLstCompatibilityToken> tkm =
					new TripleKeyMap<Integer, Integer, Integer, PCClassLevelLstCompatibilityToken>();
			for (PCClassLevelLstCompatibilityToken tok : tokens)
			{
				tkm.put(Integer.valueOf(tok.compatibilityLevel()), Integer
					.valueOf(tok.compatibilitySubLevel()), Integer.valueOf(tok
					.compatibilityPriority()), tok);
			}
			TreeSet<Integer> primarySet = new TreeSet<Integer>(REVERSE);
			primarySet.addAll(tkm.getKeySet());
			TreeSet<Integer> secondarySet = new TreeSet<Integer>(REVERSE);
			TreeSet<Integer> tertiarySet = new TreeSet<Integer>(REVERSE);
			for (Integer compatLevel : primarySet)
			{
				secondarySet.addAll(tkm.getSecondaryKeySet(compatLevel));
				for (Integer subLevel : secondarySet)
				{
					tertiarySet.addAll(tkm.getTertiaryKeySet(compatLevel,
						subLevel));
					for (Integer priority : tertiarySet)
					{
						PCClassLevelLstCompatibilityToken tok =
								tkm.get(compatLevel, subLevel, priority);
						try
						{
							if (tok.parse(context, pcclass, value, level))
							{
								return true;
							}
							context.decommit();
						}
						catch (PersistenceLayerException e)
						{
							context.decommit();
							Logging.rewindParseMessages();
							Logging.replayParsedMessages();
							Logging.errorPrint("Error parsing PCCLASS Token '"
									+ key + "' for " + pcclass.getDisplayName()
									+ " in " + context.graph.getSourceURI()
									+ ": " + value);
						}
					}
					tertiarySet.clear();
				}
				secondarySet.clear();
			}
		}
		return false;
	}
}
