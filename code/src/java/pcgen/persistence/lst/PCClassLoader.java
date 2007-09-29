/*
 * PCClassLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.ReverseIntegerComparator;
import pcgen.base.util.TripleKeyMap;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * 
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PCClassLoader extends LstLeveledObjectFileLoader<PCClass>
{
	/** Creates a new instance of PCClassLoader */
	public PCClassLoader()
	{
		super();
	}

	@Override
	public PCClass parseLine(LoadContext context, PCClass target,
		String lstLine, CampaignSourceEntry source)
	{
		int tabLoc = lstLine.indexOf("\t");
		String firstToken;
		String restOfLine;
		if (tabLoc == -1)
		{
			// Error??
			firstToken = lstLine;
			restOfLine = "";
		}
		else
		{
			firstToken = lstLine.substring(0, tabLoc);
			restOfLine = lstLine.substring(tabLoc + 1);
		}

		if (target == null)
		{
			if (firstToken.startsWith("CLASS:"))
			{
				String className = firstToken.substring(6);
				target =
						context.ref.silentlyGetConstructedCDOMObject(
							getLoadClass(), className);
				if (target == null)
				{
					target =
							context.ref.constructCDOMObject(getLoadClass(),
								className);
					target.setName(className);
					target.setSourceCampaign(source.getCampaign());
					target.setSourceURI(source.getURI());
				}
			}
			else
			{
				Logging.errorPrint("How did I see this line: " + lstLine);
				return null;
			}
		}
		try
		{
			int lvl = Integer.parseInt(firstToken);
			PCClassLevelLoader.parseLine(context, target, restOfLine, source,
				lvl);
		}
		catch (NumberFormatException nfe)
		{
			if (firstToken.startsWith("SUBCLASS:"))
			{
				if (lstLine.indexOf("\t") == -1)
				{
					Logging.errorPrint("Expected SUBCLASS to have "
						+ "additional Tags in " + source.getURI()
						+ " (e.g. COST is a required Tag in a SUBCLASS)");
				}
				String subClassKey = firstToken.substring(9);
				// TODO FIXME Should this really be through LoadContext??
				SubClass sc = target.getSubClassKeyed(subClassKey);
				if (sc == null)
				{
					sc = new SubClass();
					sc.setSourceCampaign(source.getCampaign());
					sc.setSourceURI(source.getURI());
					sc.setName(subClassKey);
					target.addSubClass(sc);
				}
				parseSubClassLine(context, sc, restOfLine, source);
			}
			else if (firstToken.startsWith("SUBCLASSLEVEL:"))
			{

				// FIXME
				// SubClassLoader.parseLine(context, sc, restOfLine, source);
			}
			else if (firstToken.startsWith("SUBSTITUTIONCLASS:"))
			{
				if (lstLine.indexOf("\t") == -1)
				{
					Logging.errorPrint("Expected SUBSTITUTIONCLASS to have "
						+ "additional Tags in " + source.getURI()
						+ " (otherwise SUBSTITUTIONCLASS has no value)");
				}
				String subClassKey = firstToken.substring(18);
				// TODO FIXME Should this really be through LoadContext??
				SubstitutionClass sc =
						target.getSubstitutionClassKeyed(subClassKey);
				if (sc == null)
				{
					sc = new SubstitutionClass();
					sc.setSourceCampaign(source.getCampaign());
					sc.setSourceURI(source.getURI());
					sc.setName(subClassKey);
					target.addSubstitutionClass(sc);
				}
				parseClassLine(context, sc, restOfLine, source);
			}
			else if (firstToken.startsWith("SUBSTITUTIONCLASSLEVEL:"))
			{
				// FIXME
				// SubstitutionClassLoader.parseLine(context, sc, restOfLine,
				// source);
			}
			else if (firstToken.startsWith("CLASS:"))
			{
				PCClass thisTarget =
						context.ref.silentlyGetConstructedCDOMObject(
							getLoadClass(), firstToken.substring(6));
				if (thisTarget != target)
				{
					target =
							context.ref.constructCDOMObject(getLoadClass(),
								firstToken.substring(6));
					target.setName(firstToken.substring(6));
					target.setSourceCampaign(source.getCampaign());
					target.setSourceURI(source.getURI());
				}
				parseClassLine(context, target, restOfLine, source);
			}
			else
			{
				Logging.errorPrint("Not sure what to do with: " + lstLine);
			}
		}
		return target;
	}

	private void parseSubClassLine(LoadContext context, SubClass sc,
		String restOfLine, CampaignSourceEntry source)
	{
		StringTokenizer colToken = new StringTokenizer(restOfLine, "\t");
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
			SubClassLstToken subclasstoken =
					TokenStore.inst().getToken(SubClassLstToken.class, key);

			if (subclasstoken != null)
			{
				LstUtils.deprecationCheck(subclasstoken, sc, value);
				if (subclasstoken.parse(context, sc, value))
				{
					continue;
				}
			}

			parseToken(context, sc, key, value, source);
		}
	}

	private void parseClassLine(LoadContext context, PCClass target,
		String lstLine, CampaignSourceEntry source)
	{
		StringTokenizer colToken = new StringTokenizer(lstLine, "\t");
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
			parseToken(context, target, key, value, source);
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject,
	 *      java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public PCClass parseLine(PCClass target, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		PCClass pcClass = target;

		/*
		 * FIXME TODO This should probably be done AFTER SUB*CLASS string
		 * checking, as a null PCClass with SUB* items is meaningless... and an
		 * error that should be flagged to the user - thpr 1/10/07
		 */
		if (pcClass == null)
		{
			pcClass = new PCClass();
		}

		if (lstLine.startsWith("SUBCLASS:")
			|| lstLine.startsWith("SUBCLASSLEVEL:"))
		{
			SubClass subClass = null;

			if (lstLine.startsWith("SUBCLASS:"))
			{
				if (lstLine.indexOf("\t") == -1)
				{
					Logging.errorPrint("Expected SUBCLASS to have "
						+ "additional Tags in " + source.getURI()
						+ " (e.g. COST is a required Tag in a SUBCLASS)");
				}
				final String n = lstLine.substring(9, lstLine.indexOf("\t"));
				subClass = pcClass.getSubClassKeyed(n);

				if (subClass == null)
				{
					subClass = new SubClass();
					subClass.setSourceCampaign(source.getCampaign());
					subClass.setSourceURI(source.getURI());
					pcClass.addSubClass(subClass);
				}
			}
			else
			{
				if ((pcClass.getSubClassList() != null)
					&& !pcClass.getSubClassList().isEmpty())
				{
					subClass =
							pcClass.getSubClassList().get(
								pcClass.getSubClassList().size() - 1);
					subClass.addToLevelArray(lstLine.substring(14));

					return pcClass;
				}
			}

			if (subClass != null)
			{
				SubClassLoader.parseLine(subClass, lstLine, source);
			}

			return pcClass;
		}

		if (lstLine.startsWith("SUBSTITUTIONCLASS:")
			|| lstLine.startsWith("SUBSTITUTIONLEVEL:"))
		{
			SubstitutionClass substitutionClass = null;

			if (lstLine.startsWith("SUBSTITUTIONCLASS:"))
			{
				if (lstLine.indexOf("\t") > 0)
				{
					substitutionClass =
							pcClass.getSubstitutionClassKeyed(lstLine
								.substring(18, lstLine.indexOf("\t")));
				}
				else
				{
					substitutionClass =
							pcClass.getSubstitutionClassKeyed(lstLine
								.substring(18));
				}

				if (substitutionClass == null)
				{
					substitutionClass = new SubstitutionClass();
					substitutionClass.setSourceCampaign(source.getCampaign());
					substitutionClass.setSourceURI(source.getURI());
					pcClass.addSubstitutionClass(substitutionClass);
				}
			}
			else
			{
				if ((pcClass.getSubstitutionClassList() != null)
					&& !pcClass.getSubstitutionClassList().isEmpty()
					&& lstLine.length() > 18)
				{
					substitutionClass =
							(SubstitutionClass) pcClass
								.getSubstitutionClassList()
								.get(
									pcClass.getSubstitutionClassList().size() - 1);
					substitutionClass.addToLevelArray(lstLine.substring(18));

					return pcClass;
				}
			}

			if (substitutionClass != null)
			{
				SubstitutionClassLoader.parseLine(substitutionClass, lstLine,
					source);
			}

			return pcClass;
		}

		return parseClassLine(lstLine, source, pcClass, false);
	}

	private PCClass parseClassLine(String lstLine, CampaignSourceEntry source,
		PCClass pcClass, boolean bRepeating) throws PersistenceLayerException
	{

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int iLevel = 0;
		boolean isNumber = true;

		String repeatTag = null;

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCClassLstToken.class);
		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			PCClassLstToken token = (PCClassLstToken) tokenMap.get(key);

			if (colString.startsWith("CLASS:"))
			{
				isNumber = false;

				String name = colString.substring(6);

				if ((!name.equals(pcClass.getKeyName()))
					&& (name.indexOf(".MOD") < 0))
				{
					// TODO - This should never happen
					completeObject(source, pcClass);
					pcClass = new PCClass();
					pcClass.setName(name);
					pcClass.setSourceURI(source.getURI());
					pcClass.setSourceCampaign(source.getCampaign());
				}
				// need to grab PCClass instance for this .MOD minus the .MOD
				// part of the name
				else if (name.endsWith(".MOD"))
				{
					pcClass =
							Globals.getClassKeyed(name.substring(0, name
								.length() - 4));
				}
			}
			else if (!(pcClass instanceof SubClass)
				&& !(pcClass instanceof SubstitutionClass) && (isNumber))
			{
				try
				{
					String thisLevel;
					int rlLoc = colString.indexOf(":REPEATLEVEL:");
					if (rlLoc == -1)
					{
						thisLevel = colString;
					}
					else
					{
						thisLevel = colString.substring(0, rlLoc);
						repeatTag = colString.substring(rlLoc + 13);
					}
					iLevel = Integer.parseInt(thisLevel);
				}
				catch (NumberFormatException nfe)
				{
					// I think we can ignore this, as
					// it's supposed to be the level #
					// but could be almost anything else
					Logging.debugPrint("Expected a level value, but got '"
						+ colString + "' instead in " + source.getURI(), nfe);
				}

				isNumber = false;

				continue;
			}
			else if (colString.startsWith("CHECK"))
			{
				continue;
			}
			else if (colString.startsWith("HASSUBCLASS:"))
			{
				pcClass.setHasSubClass(true);
			}
			else if (colString.startsWith("HASSUBSTITUTIONLEVEL:"))
			{
				pcClass.setHasSubstitutionClass(true);
			}
			else if (colString.startsWith("REPEATLEVEL:"))
			{
				Logging.deprecationPrint("REPEATLEVEL: should be attached to the level identifier\n Floating REPEATLEVEL: syntax has been deprecated");
				if (!bRepeating)
				{
					repeatTag = colString.substring(12);
				}
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, pcClass, value);
				if (!token.parse(pcClass, value, iLevel))
				{
					Logging.debugPrint("Error parsing pcclass "
						+ pcClass.getDisplayName() + ':' + source.getURI()
						+ ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTagLevel(pcClass, colString, iLevel))
			{
				continue;
			}
			else
			{
				if (!(pcClass instanceof SubClass)
					&& !(pcClass instanceof SubstitutionClass))
				{
					Logging.debugPrint("Illegal class info tag '" + colString
						+ "' in " + source.getURI());
				}
			}

			isNumber = false;
		}

		//
		// Process after all other tokens so 'order' is preserved
		//
		if ((repeatTag != null) && (iLevel > 0))
		{
			parseRepeatClassLevel(lstLine, source, pcClass, iLevel, repeatTag);
		}
		return pcClass;
	}

	private void parseRepeatClassLevel(String lstLine,
		CampaignSourceEntry source, PCClass pcClass, int iLevel,
		String colString) throws PersistenceLayerException
	{
		//
		// REPEAT:<level increment>|<consecutive>|<max level>
		//
		final StringTokenizer repeatToken = new StringTokenizer(colString, "|");
		final int tokenCount = repeatToken.countTokens();
		int lvlIncrement = 1000; // an arbitrarily large number...
		int consecutive = 0; // 0 means don't skip any
		int maxLevel = 100; // an arbitrarily large number...
		if (pcClass.hasMaxLevel())
		{
			maxLevel = pcClass.getMaxLevel();
		}
		if (tokenCount > 0)
		{
			try
			{
				lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Level Increment info '"
					+ colString + "' in " + source.getURI(), nfe);
			}
		}
		boolean oldSyntax = false;
		if (tokenCount > 1)
		{
			boolean consumed = false;
			String tokenTwo = repeatToken.nextToken();
			if (tokenTwo.startsWith("SKIP="))
			{
				tokenTwo = tokenTwo.substring(5);
			}
			else if (tokenTwo.startsWith("MAX="))
			{
				if (tokenCount > 2)
				{
					Logging.errorPrint("MAX= cannot be followed by another item in REPEATLEVEL.  SKIP= must appear before MAX=");
				}
				String maxString = tokenTwo.substring(4);
				try
				{
					maxLevel = Integer.parseInt(maxString);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Non-Numeric Max Level info MAX='" + maxLevel
						+ "' in " + source.getURI(), nfe);
				}
				consumed = true;
			}
			else
			{
				oldSyntax = true;
			}
			if (!consumed)
			{
				try
				{
					consecutive = Integer.parseInt(tokenTwo);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Non-Numeric Consecutive Level info '"
						+ colString + "' in " + source.getURI(), nfe);
				}
			}
		}
		if (tokenCount > 2)
		{
			String tokenThree = repeatToken.nextToken();
			String maxString;
			if (!oldSyntax && tokenThree.startsWith("MAX="))
			{
				maxString = tokenThree.substring(4);
			}
			else
			{
				maxString = tokenThree;
			}
			try
			{
				maxLevel = Integer.parseInt(maxString);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Max Level info '" + colString
					+ "' in " + source.getURI(), nfe);
			}
		}

		final int tabIndex = lstLine.indexOf(SystemLoader.TAB_DELIM);
		int count = consecutive - 1; // first one already added by processing
		// of lstLine, so skip it
		for (int lvl = iLevel + lvlIncrement; lvl <= maxLevel; lvl +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				parseClassLine(Integer.toString(lvl)
					+ lstLine.substring(tabIndex), source, pcClass, true);
			}
			if (consecutive != 0)
			{
				if (count == 0)
				{
					count = consecutive;
				}
				else
				{
					--count;
				}
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	@Override
	protected PCClass getObjectKeyed(String aKey)
	{
		return Globals.getClassKeyed(aKey.startsWith("CLASS:") ? aKey
			.substring(6) : aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	@Override
	protected void finishObject(final PObject target)
	{
		final List<Prerequisite> preReqList = target.getPreReqList();
		if (preReqList != null)
		{
			for (Prerequisite preReq : preReqList)
			{
				if ("VAR".equalsIgnoreCase(preReq.getKind()))
				{
					preReq.setSubKey("CLASS:" + target.getKeyName());
				}

			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final PCClass objToForget)
	{
		Globals.getClassList().remove(objToForget);
	}

	public static String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer().append(aInt).append("|").append(colString)
			.toString();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		// TODO - Create Globals.addClass( final PCClass aClass )
		Globals.getClassList().add((PCClass) pObj);
	}

	public void parseToken(LoadContext context, PCClass pcclass, String key,
		String value, CampaignSourceEntry source)
	{
		PCClassUniversalLstToken univtoken =
				TokenStore.inst().getToken(PCClassUniversalLstToken.class, key);
		PCClassClassLstToken classtoken =
				TokenStore.inst().getToken(PCClassClassLstToken.class, key);

		if (classtoken == null)
		{
			if (!processClassCompatible(context, pcclass, key, value, source))
			{
				processUniversalToken(context, pcclass, key, value, source,
					univtoken);
			}
		}
		else
		{
			processClassToken(context, pcclass, key, value, source, classtoken);
		}
	}

	static void processUniversalToken(LoadContext context, PObject po,
		String key, String value, CampaignSourceEntry source,
		PCClassUniversalLstToken univtoken)
	{
		if (univtoken == null)
		{
			if (processUniversalCompatible(context, po, key, value, source))
			{
				context.commit();
			}
			else
			{
				context.decommit();
				Logging.clearParseMessages();
				try
				{
					if (PObjectLoader.parseTag(context, po, key, value))
					{
						context.commit();
					}
					else
					{
						context.decommit();
						Logging.errorPrint("Illegal PCClass Token '" + key
							+ "' for " + po.getDisplayName() + " in "
							+ source.getURI() + " of " + source.getCampaign()
							+ ".");
					}
				}
				catch (PersistenceLayerException e)
				{
					context.decommit();
					Logging
						.errorPrint("Error parsing PCClass Token '" + key
							+ "' for " + po.getDisplayName() + " in "
							+ source.getURI() + " of " + source.getCampaign()
							+ ".");
				}
			}
		}
		else
		{
			LstUtils.deprecationCheck(univtoken, po, value);
			try
			{
				if (univtoken.parse(context, po, value))
				{
					context.commit();
				}
				else
				{
					context.decommit();
					if (processUniversalCompatible(context, po, key, value,
						source))
					{
						context.commit();
						Logging.clearParseMessages();
					}
					else
					{
						context.decommit();
						Logging.rewindParseMessages();
						Logging.replayParsedMessages();
						Logging.errorPrint("Error parsing token " + key
							+ " in pcclass " + po.getDisplayName() + ':'
							+ source.getURI() + ':' + value + "\"");
					}
				}
			}
			catch (PersistenceLayerException e)
			{
				context.decommit();
				Logging.errorPrint("Error parsing PCClass Token '" + key
					+ "' for " + po.getDisplayName() + " in " + source.getURI()
					+ " of " + source.getCampaign() + ".");
			}
		}
	}

	private static boolean processUniversalCompatible(LoadContext context,
		PObject po, String key, String value, CampaignSourceEntry source)
	{
		Collection<? extends CDOMCompatibilityToken<PObject>> tokens =
				TokenStore.inst().getCompatibilityToken(
					PCClassUniversalLstCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<PObject>> tkm =
					new TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<PObject>>();
			for (CDOMCompatibilityToken<PObject> tok : tokens)
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
						CDOMCompatibilityToken<PObject> tok =
								tkm.get(level, subLevel, priority);
						try
						{
							if (tok.parse(context, po, value))
							{
								return true;
							}
						}
						catch (PersistenceLayerException e)
						{
							Logging.errorPrint("Error parsing PCClass Token '"
								+ key + "' for " + po.getDisplayName() + " in "
								+ source.getURI() + " of "
								+ source.getCampaign() + ".");
						}
						context.decommit();
					}
					tertiarySet.clear();
				}
				secondarySet.clear();
			}
		}
		return false;
	}

	private void processClassToken(LoadContext context, PCClass pcclass,
		String key, String value, CampaignSourceEntry source,
		PCClassClassLstToken classtoken)
	{
		LstUtils.deprecationCheck(classtoken, pcclass, value);
		try
		{
			if (classtoken.parse(context, pcclass, value))
			{
				context.commit();
			}
			else
			{
				context.decommit();
				Logging.markParseMessages();
				if (processClassCompatible(context, pcclass, key, value, source))
				{
					context.commit();
					Logging.clearParseMessages();
				}
				else
				{
					context.decommit();
					Logging.rewindParseMessages();
					Logging.replayParsedMessages();
					Logging.errorPrint("Error parsing token " + key
						+ " in pcclass " + pcclass.getDisplayName() + ':'
						+ source.getURI() + ':' + value + "\"");
				}
			}
		}
		catch (PersistenceLayerException e)
		{
			context.decommit();
			Logging.errorPrint("Error parsing " + getLoadClass().getName()
				+ " Token '" + key + "' for " + pcclass.getDisplayName()
				+ " in " + source.getURI() + " of " + source.getCampaign()
				+ ".");
		}
	}

	private static final ReverseIntegerComparator REVERSE =
			new ReverseIntegerComparator();

	private boolean processClassCompatible(LoadContext context,
		PCClass pcclass, String key, String value, CampaignSourceEntry source)
	{
		Collection<? extends CDOMCompatibilityToken<PCClass>> tokens =
				TokenStore.inst().getCompatibilityToken(
					PCClassClassLstCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<PCClass>> tkm =
					new TripleKeyMap<Integer, Integer, Integer, CDOMCompatibilityToken<PCClass>>();
			for (CDOMCompatibilityToken<PCClass> tok : tokens)
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
						CDOMCompatibilityToken<PCClass> tok =
								tkm.get(level, subLevel, priority);
						try
						{
							if (tok.parse(context, pcclass, value))
							{
								return true;
							}
							context.decommit();
						}
						catch (PersistenceLayerException e)
						{
							Logging.errorPrint("Error parsing "
								+ getLoadClass().getName() + " Token '" + key
								+ "' for " + pcclass.getDisplayName() + " in "
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

	/*
	 * FIXME parseToken should only be used for PCClass - what about
	 * PCClassLevel?
	 */
	@Override
	public Class<PCClass> getLoadClass()
	{
		return PCClass.class;
	}
}
