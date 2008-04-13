package pcgen.rules.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
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
						CDOMPCClassLevel pcl;
						if (objectName == null)
						{
							pcl = activePCClass.getClassLevel(level);
						}
						else
						{
							pcl = activePCClass.getRepeatLevel(level, objectName);
							/*
							 * TODO NEED TO Load REPEATLEVEL
							 */
						}
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
			Collection<CampaignSourceEntry> files)
	{
		for (CampaignSourceEntry cse : files)
		{
			context.setExtractURI(cse.getURI());
			URI writeURI = cse.getWriteURI();
			String path = writeURI.getPath().substring(1);
			File f = new File(path);
			ensureCreated(f.getParentFile());
			try
			{
				unloadFile(context, cse, f);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void unloadFile(LoadContext context, CampaignSourceEntry cse, File f)
			throws FileNotFoundException
	{
		TreeSet<String> set = new TreeSet<String>();
		for (CDOMPCClass pcc : context.ref
				.getConstructedCDOMObjects(CDOMPCClass.class))
		{
			if (cse.getURI().equals(pcc.get(ObjectKey.SOURCE_URI)))
			{
				StringBuilder sb = new StringBuilder();
				sb.append("CLASS:");
				sb.append(classLoader.unparseObject(context, cse, pcc));
				sb.append('\n');
				SubClassCategory subClCategory = SubClassCategory
						.getConstant(pcc.getKeyName());
				for (CDOMSubClass sc : context.ref.getConstructedCDOMObjects(
						CDOMSubClass.class, subClCategory))
				{
					sb.append("SUBCLASS:");
					sb.append(subClassLoader.unparseObject(context, cse, sc));
					sb.append('\n');
					appendLevels(context, sc, sb, "SUBCLASSLEVEL:");
				}
				appendLevels(context, pcc, sb, "");
				sb.append('\n');
				set.add(sb.toString());
			}
		}
		PrintWriter pw = new PrintWriter(f);
		pw.println("#~PARAGRAPH");
		for (String s : set)
		{
			pw.print(s);
		}
		pw.close();
	}

	private void appendLevels(LoadContext context, CDOMPCClass pcc,
			StringBuilder sb, String prefix)
	{
		TreeMapToList<Integer, String> levelMap = new TreeMapToList<Integer, String>();
		for (CDOMPCClassLevel pcl : pcc.getClassLevelCollection())
		{
			StringBuilder lsb = new StringBuilder();
			String cs = StringUtil.join(context.unparse(pcl), "\t");
			if (cs.length() > 0)
			{
				Integer lvl = pcl.get(IntegerKey.LEVEL);
				lsb.append(prefix);
				lsb.append(lvl);
				lsb.append('\t');
				lsb.append(cs);
				lsb.append('\n');
				levelMap.addToListFor(lvl, lsb.toString());
			}
		}
		for (CDOMPCClassLevel pcl : pcc.getRepeatLevels())
		{
			StringBuilder lsb = new StringBuilder();
			String cs = StringUtil.join(context.unparse(pcl), "\t");
			if (cs.length() > 0)
			{
				String rep = pcl.get(StringKey.REPEAT);
				lsb.append(prefix);
				lsb.append(rep);
				lsb.append('\t');
				lsb.append(cs);
				lsb.append('\n');
				int lvl = Integer.parseInt(rep.substring(0, rep.indexOf(':')));
				levelMap.addToListFor(lvl, lsb.toString());
			}
		}
		for (Integer i : levelMap.getKeySet())
		{
			for (String cs : levelMap.getListFor(i))
			{
				sb.append(cs);
			}
		}
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
