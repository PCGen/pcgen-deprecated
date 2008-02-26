package pcgen.rules.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.inst.CDOMKit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CDOMKitLoader implements CDOMLoader<CDOMKit>
{
	private final Map<String, CDOMSubLineLoader<?>> loadMap = new HashMap<String, CDOMSubLineLoader<?>>();

	private final Class<CDOMKit> targetClass = CDOMKit.class;

	private CDOMKit activeKit = null;

	public void addLineLoader(CDOMSubLineLoader<?> loader)
	{
		// TODO check null
		// TODO check duplicate!
		loadMap.put(loader.getPrefix(), loader);
	}

	public void parseLine(LoadContext context, CDOMKit obj, String val,
			URI source) throws PersistenceLayerException
	{
		StringTokenizer st = new StringTokenizer(val, "\t");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken().trim();
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
			}
			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processSubToken(obj, "*KITTOKEN", key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else
			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
			}
		}
	}

	public void loadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> sources)
	{
		// Track which sources have been loaded already
		Set<CampaignSourceEntry> loadedSources = new HashSet<CampaignSourceEntry>();

		// Load the files themselves as thoroughly as possible
		for (CampaignSourceEntry sourceEntry : sources)
		{
			// Check if the CSE has already been loaded before loading it
			if (!loadedSources.contains(sourceEntry))
			{
				loadLstFile(context, sourceEntry.getURI());
				loadedSources.add(sourceEntry);
			}
		}
	}

	public void loadLstFile(LoadContext context, URI uri)
	{
		StringBuilder dataBuffer;
		context.setSourceURI(uri);

		try
		{
			dataBuffer = LstFileLoader.readFromURI(uri);
		}
		catch (PersistenceLayerException ple)
		{
			String message = PropertyFactory.getFormattedString(
					"Errors.LstFileLoader.LoadError", //$NON-NLS-1$
					uri, ple.getMessage());
			Logging.errorPrint(message);
			return;
		}

		final String aString = dataBuffer.toString();
		String[] fileLines = aString.split(LstFileLoader.LINE_SEPARATOR_REGEXP);

		for (int i = 0; i < fileLines.length; i++)
		{
			String line = fileLines[i];
			if ((line.length() == 0)
					|| (line.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR))
			{
				continue;
			}
			int sepLoc = line.indexOf('\t');
			String firstToken;
			String restOfLine;
			if (sepLoc == -1)
			{
				firstToken = line;
				restOfLine = null;
			}
			else
			{
				firstToken = line.substring(0, sepLoc);
				restOfLine = line.substring(sepLoc + 1);
			}

			// check for copies, mods, and forgets
			// TODO - Figure out why we need to check SOURCE in this file
			if (line.startsWith("SOURCE")) //$NON-NLS-1$
			{
				// TODO sourceMap = SourceLoader.parseLine(line,
				// sourceEntry.getURI());
			}
			else if (line.startsWith("STARTPACK:"))
			{
				String name = firstToken.substring(10);
				activeKit = getCDOMObject(context, name);
				try
				{
					parseLine(context, activeKit, restOfLine, uri);
				}
				catch (PersistenceLayerException ple)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, ple.getMessage());
					Logging.errorPrint(message);
					Logging.debugPrint("Parse error:", ple); //$NON-NLS-1$
				}
				catch (Throwable t)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, t.getMessage());
					Logging.errorPrint(message);
					Logging.errorPrint(PropertyFactory
							.getString("Errors.LstFileLoader.Ignoring"), //$NON-NLS-1$
							t);
				}
			}
			else
			{
				int colonLoc = firstToken.indexOf(':');
				if (colonLoc == -1)
				{
					Logging.errorPrint("Unsure what to do with line without "
							+ "a colon in first token: " + line + " in file: "
							+ uri);
					continue;
				}

				String prefix = firstToken.substring(0, colonLoc);
				CDOMSubLineLoader<?> loader = loadMap.get(prefix);
				if (loader == null)
				{
					Logging
							.errorPrint("Unsure what to do with line with prefix: "
									+ prefix
									+ ".  Line was: "
									+ line
									+ " in file: " + uri);
					continue;
				}
				try
				{
					subParse(context, loader, line, uri);
				}
				catch (PersistenceLayerException ple)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, ple.getMessage());
					Logging.errorPrint(message);
					Logging.debugPrint("Parse error:", ple); //$NON-NLS-1$
				}
				catch (Throwable t)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, t.getMessage());
					Logging.errorPrint(message);
					Logging.errorPrint(PropertyFactory
							.getString("Errors.LstFileLoader.Ignoring"), //$NON-NLS-1$
							t);
				}
			}
		}
	}

	private <CC> void subParse(LoadContext context,
			CDOMSubLineLoader<CC> loader, String line, URI uri)
			throws PersistenceLayerException
	{
		CC obj = loader.getCDOMObject(context);
		loader.parseLine(context, obj, line, uri);
	}

	protected CDOMKit getCDOMObject(LoadContext context, String name)
	{
		CDOMKit obj = context.ref.silentlyGetConstructedCDOMObject(targetClass,
				name);
		if (obj == null)
		{
			obj = context.ref.constructCDOMObject(targetClass, name);
		}
		return obj;
	}

	public Class<CDOMKit> getTargetClass()
	{
		return targetClass;
	}

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> languageFiles)
	{
		// TODO Auto-generated method stub
		
	}
}
