package pcgen.rules.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CDOMCompositeLineLoader implements CDOMLoader<CDOMObject>
{
	private final Map<String, CDOMLineLoader<?>> loadMap = new HashMap<String, CDOMLineLoader<?>>();

	public CDOMCompositeLineLoader()
	{
	}

	public void addLineLoader(CDOMLineLoader<?> loader)
	{
		// TODO check null
		// TODO check duplicate!
		loadMap.put(loader.getPrefix(), loader);
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

			int colonLoc = firstToken.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Unsure what to do with line without "
						+ "a colon in first token: " + line + " in file: "
						+ uri);
				continue;
			}

			if (line.startsWith("SOURCE")) //$NON-NLS-1$
			{
				// TODO sourceMap = SourceLoader.parseLine(line,
				// sourceEntry.getURI());
				continue;
			}
			// TODO colonLoc == 0
			String prefix = firstToken.substring(0, colonLoc);
			CDOMLineLoader<?> loader = getLoader(prefix);
			if (loader == null)
			{
				Logging.errorPrint("Unsure what to do with line with prefix: "
						+ prefix + ".  Line was: " + line + " in file: " + uri);
				continue;
			}
			try
			{
				subParse(context, loader, firstToken.substring(colonLoc + 1),
						restOfLine, uri);
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

	protected CDOMLineLoader<?> getLoader(String prefix)
	{
		return loadMap.get(prefix);
	}

	private <CC extends CDOMObject> void subParse(LoadContext context,
			CDOMLineLoader<CC> loader, String name, String restOfLine, URI uri)
			throws PersistenceLayerException
	{
		CC obj = loader.getCDOMObject(context, name);
		loader.parseLine(context, obj, restOfLine, uri);
	}

	public void parseLine(LoadContext context, CDOMObject obj, String val,
			URI source) throws PersistenceLayerException
	{
		throw new UnsupportedOperationException();
	}
}
