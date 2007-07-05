package pcgen.persistence.lst;

import java.util.StringTokenizer;

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
					+ colString);
				return;
			}
			else if (idxColon == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
					+ colString);
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
				if (univtoken == null)
				{
					Logging.clearParseMessages();
					try
					{
						if (!PObjectLoader.parseTag(context, pcclass
							.getClassLevel(level), key, value))
						{
							Logging.errorPrint("Illegal PCClass Level Token '"
								+ key + "' for " + pcclass.getDisplayName()
								+ " level " + level + " in " + source.getURI()
								+ " of " + source.getCampaign() + ".");
						}
					}
					catch (PersistenceLayerException e)
					{
						Logging
							.errorPrint("Error parsing PCClass Level Token '"
								+ key + "' for " + pcclass.getDisplayName()
								+ " level " + level + " in " + source.getURI()
								+ " of " + source.getCampaign() + ".");
					}
					catch (Throwable t)
					{
						Logging
							.errorPrint("Error parsing PCClass Level Token '"
								+ key + "' for " + pcclass.getDisplayName()
								+ " level " + level + " in " + source.getURI()
								+ " of " + source.getCampaign() + ".");
					}
				}
				else
				{
					LstUtils.deprecationCheck(univtoken, pcclass, value);
					try
					{
						if (!univtoken.parse(context, pcclass
							.getClassLevel(level), value))
						{
							Logging.errorPrint("Error parsing token " + key
								+ " in pcclass " + pcclass.getDisplayName()
								+ " level " + level + ':' + source.getURI()
								+ ':' + value + "\"");
						}
					}
					catch (PersistenceLayerException e)
					{
						Logging
							.errorPrint("Error parsing PCClass Level Token '"
								+ key + "' for " + pcclass.getDisplayName()
								+ " level " + level + " in " + source.getURI()
								+ " of " + source.getCampaign() + ".");
					}
				}
			}
			else
			{
				LstUtils.deprecationCheck(levelToken, pcclass, value);
				if (!levelToken.parse(context, pcclass, value, level))
				{
					Logging.errorPrint("Error parsing token " + key
						+ " in pcclass " + pcclass.getDisplayName() + ':'
						+ source.getURI() + ':' + value + "\"");
				}
			}
		}
	}
}
