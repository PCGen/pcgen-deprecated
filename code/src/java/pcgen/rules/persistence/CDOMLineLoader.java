package pcgen.rules.persistence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CDOMLineLoader<T extends CDOMObject> implements CDOMLoader<T>
{

	private final Class<T> targetClass;
	private final String targetPrefix;
	private final String targetPrefixColon;
	private final int prefixLength;

	public CDOMLineLoader(String prefix, Class<T> cl)
	{
		targetClass = cl;
		targetPrefix = prefix;
		targetPrefixColon = prefix + ":";
		prefixLength = targetPrefixColon.length();
	}

	public void parseLine(LoadContext context, T obj, String val, URI source)
			throws PersistenceLayerException
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
			if (context.processToken(obj, key, value))
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
			else if (line.startsWith(targetPrefixColon))
			{
				String name = firstToken.substring(prefixLength);
				T obj = getCDOMObject(context, name);
				try
				{
					parseLine(context, obj, restOfLine, uri);
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
				Logging.errorPrint("Unsure what to do with line: " + line
						+ " in file: " + uri);
			}
		}
	}

	public T getCDOMObject(LoadContext context, String name)
	{
		return context.ref.constructCDOMObject(targetClass, name);
	}

	public String getPrefix()
	{
		return targetPrefix;
	}

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> files)
	{
		for (CampaignSourceEntry cse : files)
		{
			lc.setExtractURI(cse.getURI());
			URI writeURI = cse.getWriteURI();
			String path = writeURI.getPath().substring(1);
			File f = new File(path);
			ensureCreated(f.getParentFile());
			try
			{
				PrintWriter pw = new PrintWriter(f);
				unloadLstFile(lc, cse, pw);
				pw.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void unloadLstFile(LoadContext lc, CampaignSourceEntry cse,
			PrintWriter pw)
	{
		for (T obj : getConstructedObjects(lc))
		{
			String unparse = StringUtil.join(lc.unparse(obj), "\t");
			if (cse.getURI().equals(obj.get(ObjectKey.SOURCE_URI)))
			{
				pw.println(new StringBuilder(targetPrefixColon).append(
						obj.getDisplayName()).append('\t').append(unparse)
						.toString());
			}
			else if (unparse.length() != 0)
			{
				// TODO This should be an error?!?
				System.err.println(obj.getDisplayName() + " " + unparse);
				System.err.println(cse.getURI() + " " + obj.get(ObjectKey.SOURCE_URI));
			}
		}
	}

	protected Collection<T> getConstructedObjects(LoadContext lc)
	{
		return lc.ref.getConstructedCDOMObjects(targetClass);
	}

	private boolean ensureCreated(File rec)
	{
		if (!rec.exists())
		{
			if (!ensureCreated(rec.getParentFile()))
			{
				return false;
			}
			return rec.mkdir();
		}
		return true;
	}
}
