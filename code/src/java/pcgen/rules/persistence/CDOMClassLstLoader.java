package pcgen.rules.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.cdom.inst.CDOMSubClass;
import pcgen.cdom.inst.CDOMSubstitutionClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CDOMClassLstLoader
{

	private final CDOMTokenLoader<CDOMPCClassLevel> levelLoader = new CDOMTokenLoader<CDOMPCClassLevel>(
			CDOMPCClassLevel.class);
	private final CDOMTokenLoader<CDOMPCClass> classLoader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);
	private final CDOMTokenLoader<CDOMSubClass> subClassLoader = new CDOMTokenLoader<CDOMSubClass>(
			CDOMSubClass.class);
	private final CDOMTokenLoader<CDOMSubstitutionClass> substitutionLoader = new CDOMTokenLoader<CDOMSubstitutionClass>(
			CDOMSubstitutionClass.class);

	private CDOMPCClass activePCClass = null;
	private boolean isClassActive = false;

	private CDOMSubClass activeSubClass = null;
	private boolean isSubClassActive = false;

	private CDOMSubstitutionClass activeSubstitutionClass = null;
	private boolean isSubstitutionClassActive = false;

	public CDOMClassLstLoader()
	{
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
			if (line.startsWith("SOURCE")) //$NON-NLS-1$
			{
				// TODO sourceMap = SourceLoader.parseLine(line,
				// sourceEntry.getURI());
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
			// TODO colonLoc == 0

			String prefix;
			String objectName;
			if (colonLoc == -1)
			{
				prefix = firstToken;
				objectName = null;
			}
			else
			{
				prefix = firstToken.substring(0, colonLoc);
				objectName = firstToken.substring(colonLoc + 1);
			}

			try
			{
				if ("CLASS".equals(prefix))
				{
					isSubClassActive = false;
					isSubstitutionClassActive = false;
					if (!isClassActive
							|| !objectName.equals(activePCClass.getKeyName()))
					{
						// New Class...
						isClassActive = true;
						activePCClass = classLoader.getCDOMObject(context,
								objectName);
					}
					subParse(context, classLoader, activePCClass, restOfLine,
							uri);
				}
				else if ("SUBCLASS".equals(prefix))
				{
					isClassActive = false;
					isSubstitutionClassActive = false;
					if (!isSubClassActive
							|| !objectName.equals(activeSubClass.getKeyName()))
					{
						isSubClassActive = true;
						activeSubClass = subClassLoader.getCDOMObject(context,
								objectName);
					}
					SubClassCategory cat = SubClassCategory
							.getConstant(activePCClass.getKeyName());
					context.ref.reassociateCategory(cat, activeSubClass);
					subParse(context, subClassLoader, activeSubClass,
							restOfLine, uri);
					continue;
				}
				else if ("SUBCLASSLEVEL".equals(prefix))
				{
					isClassActive = false;
					isSubstitutionClassActive = false;
					if (!isSubClassActive)
					{
						Logging.errorPrint("Found SUBCLASSLEVEL "
								+ "line without SUBCLASS in " + "Class LST: "
								+ line);
						continue;
					}
					try
					{
						int level = Integer.parseInt(objectName);
						CDOMPCClassLevel pcl = activeSubClass
								.getClassLevel(level);
						subParse(context, levelLoader, pcl, restOfLine, uri);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Found unparseable SUBCLASSLEVEL "
								+ "line in Class LST: " + line);
					}
				}
				else if ("SUBSTITUTIONCLASS".equals(prefix))
				{
					isClassActive = false;
					isSubClassActive = false;
					/*
					 * TODO NEED TO DETECT CHANGE WITHOUT LEVELS
					 */
					if (!isSubstitutionClassActive
							|| !objectName.equals(activeSubstitutionClass
									.getKeyName()))
					{
						activeSubstitutionClass = substitutionLoader
								.getCDOMObject(context, objectName);
						isSubstitutionClassActive = true;
					}
					/*
					 * TODO CATEGORY?
					 */
					// SubClassCategory cat = SubClassCategory
					// .getConstant(activePCClass.getKeyName());
					// context.ref.reassociateCategory(cat, activeSubClass);
					subParse(context, substitutionLoader,
							activeSubstitutionClass, restOfLine, uri);
				}
				else if ("SUBSTITUTIONLEVEL".equals(prefix))
				{
					isClassActive = false;
					isSubClassActive = false;
					if (!isSubstitutionClassActive)
					{
						Logging.errorPrint("Found SUBSTITUTIONLEVEL "
								+ "line without SUBSTITUTIONCLASS in "
								+ "Class LST: " + line);
						continue;
					}
					try
					{
						int level = Integer.parseInt(objectName);
						CDOMPCClassLevel pcl = activeSubstitutionClass
								.getClassLevel(level);
						subParse(context, levelLoader, pcl, restOfLine, uri);
					}
					catch (NumberFormatException e)
					{
						Logging
								.errorPrint("Found unparseable SUBSTITUTIONLEVEL "
										+ "line in Class LST: " + line);
					}
				}
				else
				{
					isClassActive = false;
					isSubClassActive = false;
					isSubstitutionClassActive = false;
					/*
					 * Must be a level
					 */
					try
					{
						int level = Integer.parseInt(prefix);
						if (activePCClass == null)
						{
							Logging.errorPrint("Found Class Level line "
									+ "without a Class: " + line);
							continue;
						}
						CDOMPCClassLevel pcl = activePCClass
								.getClassLevel(level);
						/*
						 * TODO NEED TO PROCESS REPEATLEVEL
						 */
						levelLoader.parseLine(context, pcl, restOfLine, uri);
					}
					catch (NumberFormatException e)
					{
						Logging
								.errorPrint("Found unparseable line in Class LST: "
										+ line);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Parse Error in Class LST: " + line
								+ " " + e.getLocalizedMessage());
					}
				}
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

	private <CC extends CDOMObject> void subParse(LoadContext context,
			CDOMTokenLoader<CC> loader, CC obj, String restOfLine, URI uri)
			throws PersistenceLayerException
	{
		loader.parseLine(context, obj, restOfLine, uri);
	}

	public void unloadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> classFiles)
	{
		// TODO Auto-generated method stub
		
	}

}
