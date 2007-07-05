package pcgen.persistence.lst;

import java.util.Collection;
import java.util.TreeSet;

import pcgen.base.util.ReverseIntegerComparator;
import pcgen.base.util.TripleKeyMap;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public abstract class GenericLstLoader<T extends PObject> extends
		LstObjectFileLoader<T>
{

	public abstract Class<? extends CDOMToken<T>> getTokenClass();

	public abstract Class<? extends CDOMCompatibilityToken<T>> getCompatibilityTokenClass();

	@Override
	public void parseToken(LoadContext context, T pobj, String key,
		String value, CampaignSourceEntry source)
	{
		CDOMToken<T> token = TokenStore.inst().getToken(getTokenClass(), key);

		if (token == null)
		{
			if (!processCompatible(context, pobj, key, value, source))
			{
				Logging.clearParseMessages();
				try
				{
					if (!PObjectLoader.parseTag(context, pobj, key, value))
					{
						Logging.errorPrint("Illegal "
							+ getLoadClass().getName() + " Token '" + key
							+ "' for " + pobj.getDisplayName() + " in "
							+ source.getURI() + " of " + source.getCampaign()
							+ ".");
					}
				}
				catch (PersistenceLayerException e)
				{
					Logging
						.errorPrint("Error parsing " + getLoadClass().getName()
							+ " Token '" + key + "' for "
							+ pobj.getDisplayName() + " in " + source.getURI()
							+ " of " + source.getCampaign() + ".");
				}
			}
		}
		else
		{
			LstUtils.deprecationCheck(token, pobj, value);
			try
			{
				if (!token.parse(context, pobj, value))
				{
					Logging.markParseMessages();
					if (processCompatible(context, pobj, key, value, source))
					{
						Logging.clearParseMessages();
					}
					else
					{
						Logging.rewindParseMessages();
						Logging.replayParsedMessages();
						Logging.errorPrint("Error parsing token " + key
							+ " in " + getLoadClass().getName() + " "
							+ pobj.getDisplayName() + ':' + source.getURI()
							+ ':' + value + "\"");
					}
				}
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Error parsing " + getLoadClass().getName()
					+ " Token '" + key + "' for " + pobj.getDisplayName()
					+ " in " + source.getURI() + " of " + source.getCampaign()
					+ ".");
			}
		}
	}

	private static final ReverseIntegerComparator REVERSE =
			new ReverseIntegerComparator();

	private boolean processCompatible(LoadContext context, T pobj, String key,
		String value, CampaignSourceEntry source)
	{
		Collection<? extends CDOMCompatibilityToken<T>> tokens =
				TokenStore.inst().getCompatibilityToken(
					getCompatibilityTokenClass(), key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<T>> tkm =
					new TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<T>>();
			for (CDOMCompatibilityToken<T> tok : tokens)
			{
				tkm.put(Integer.valueOf(tok.compatibilityLevel()), Integer
					.valueOf(tok.compatibilitySubLevel()), Integer.valueOf(tok
					.compatibilityPriority()), tok);
			}
			TreeSet<Integer> primarySet = new TreeSet<Integer>(REVERSE);
			primarySet.addAll(tkm.getKeySet());
			TreeSet<Integer> secondarySet = new TreeSet<Integer>(REVERSE);
			TreeSet<Integer> tertiarySet = new TreeSet<Integer>(REVERSE);
			for (Integer level : primarySet)
			{
				secondarySet.addAll(tkm.getSecondaryKeySet(level));
				for (Integer subLevel : secondarySet)
				{
					tertiarySet.addAll(tkm.getTertiaryKeySet(level, subLevel));
					for (Integer priority : tertiarySet)
					{
						CDOMCompatibilityToken<T> tok =
								tkm.get(level, subLevel, priority);
						try
						{
							if (tok.parse(context, pobj, value))
							{
								return true;
							}
						}
						catch (PersistenceLayerException e)
						{
							Logging.errorPrint("Error parsing "
								+ getLoadClass().getName() + " Token '" + key
								+ "' for " + pobj.getDisplayName() + " in "
								+ source.getURI() + " of "
								+ source.getCampaign() + ".");
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
