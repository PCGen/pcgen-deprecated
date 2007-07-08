/*
 * PObjectLoader.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import pcgen.base.util.ReverseIntegerComparator;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PObjectLoader
{

	/**
	 * Creates a new instance of PObjectLoader Private since instances need
	 * never be created and API methods are public and static
	 */

	private PObjectLoader()
	{
		// Empty Constructor
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or
	 * other source, such as an LST editor) It applies the value of the tag to
	 * the provided PObject.
	 * 
	 * @param obj
	 *            PObject which the tag will be applied to
	 * @param aTag
	 *            String tag and value to parse
	 * @return boolean true if the tag is parsed; else false.
	 * @throws PersistenceLayerException
	 */
	public static boolean parseTag(PObject obj, String aTag)
		throws PersistenceLayerException
	{
		return parseTagLevel(obj, aTag, -9);
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or
	 * other source, such as an LST editor) It applies the value of the tag to
	 * the provided PObject If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag] A level of -9
	 * or lower is treated as "at all levels."
	 * 
	 * @param obj
	 *            PObject which the tag will be applied to
	 * @param aTag
	 *            String tag and value to parse
	 * @param anInt
	 *            int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 * @throws PersistenceLayerException
	 */
	public static boolean parseTagLevel(PObject obj, String aTag, int anInt)
		throws PersistenceLayerException
	{
		if ((obj == null) || (aTag.length() < 1))
		{
			return false;
		}

		obj.setNewItem(false);

		aTag.charAt(0);

		boolean result = false;
		int colonIdx = aTag.indexOf(':');
		if (colonIdx < 0)
		{
			return false;
		}
		String key = aTag.substring(0, colonIdx);
		String value = aTag.substring(colonIdx + 1);
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(GlobalLstToken.class);
		LstToken token = tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			result = ((GlobalLstToken) token).parse(obj, value, anInt);
		}
		else
		{
			result = true;
			if (aTag.startsWith("CAMPAIGN:") && !(obj instanceof Campaign))
			{
				// blank intentionally
			}
			else if (PreParserFactory.isPreReqString(aTag)
				|| aTag.startsWith("RESTRICT:"))
			{
				if (aTag.equalsIgnoreCase("PRE:.CLEAR"))
				{
					obj.clearPreReq();
				}
				else
				{
					aTag =
							CoreUtility.replaceAll(aTag, "<this>", obj
								.getKeyName());
					try
					{
						PreParserFactory factory =
								PreParserFactory.getInstance();
						obj.addPreReq(factory.parse(aTag), anInt);
					}
					catch (PersistenceLayerException ple)
					{
						throw new PersistenceLayerException(
							"Unable to parse a prerequisite: "
								+ ple.getMessage());
					}
				}
			}
			else
			{
				result = false;
			}
		}

		return result;
	}

	public static boolean parseTag(LoadContext context, PObject wp, String key,
		String value) throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(GlobalLstToken.class);
		LstToken token = tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, wp, value);
			boolean globalParse;
			try
			{
				globalParse =
						((GlobalLstToken) token).parse(context, wp, value);
			}
			catch (Throwable t)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
					"Error parsing token " + key + " in "
						+ wp.getClass().getName() + " " + wp.getDisplayName()
						+ ": " + t.getLocalizedMessage()
						+ Arrays.toString(t.getStackTrace()));
				globalParse = false;
			}
			if (globalParse)
			{
				return true;
			}
			else
			{
				Logging.markParseMessages();
				if (processCompatible(context, wp, key, value))
				{
					Logging.clearParseMessages();
					return true;
				}
				else
				{
					Logging.rewindParseMessages();
					Logging.replayParsedMessages();
					Logging.errorPrint("Error parsing token " + key + " in "
						+ wp.getClass().getName() + " " + wp.getDisplayName());
					return false;
				}
			}
		}
		else
		{
			if (key.startsWith("PRE") || key.startsWith("!PRE"))
			{
				if (key.toUpperCase().equals("PRE:.CLEAR"))
				{
					wp.clearPreReq();
				}
				else
				{
					value =
							CoreUtility.replaceAll(value, "<this>", wp
								.getKeyName());
					try
					{
						PreParserFactory factory =
								PreParserFactory.getInstance();
						wp.addPreReq(factory.parse(key + ":" + value));
					}
					catch (PersistenceLayerException ple)
					{
						throw new PersistenceLayerException(
							"Unable to parse a prerequisite: "
								+ ple.getMessage());
					}
				}
				return true;
			}
			return false;
		}
	}

	private static final ReverseIntegerComparator REVERSE =
			new ReverseIntegerComparator();

	private static boolean processCompatible(LoadContext context,
		CDOMObject po, String key, String value)
		throws PersistenceLayerException
	{
		Collection<GlobalLstCompatibilityToken> tokens =
				TokenStore.inst().getCompatibilityToken(
					GlobalLstCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, GlobalLstCompatibilityToken> tkm =
					new TripleKeyMap<Integer, Integer, Integer, GlobalLstCompatibilityToken>();
			for (GlobalLstCompatibilityToken tok : tokens)
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
						GlobalLstCompatibilityToken tok =
								tkm.get(level, subLevel, priority);
						boolean compatParse;
						try
						{
							compatParse = tok.parse(context, po, value);
						}
						catch (Throwable t)
						{
							Logging.addParseMessage(Logging.LST_ERROR,
								"Error parsing token " + key + " in "
									+ po.getClass().getName() + " "
									+ po.getDisplayName() + ": "
									+ t.getLocalizedMessage());
							compatParse = false;
						}
						if (compatParse)
						{
							return true;
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
