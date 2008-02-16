/*
 * ChooseLoader.java
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 * Created on February 17, 2007
 *
 * $Id: AddLoader.java 2077 2007-01-27 16:45:58Z thpr $
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.ReverseIntegerComparator;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.CompoundAndFilter;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.NegatingFilter;
import pcgen.cdom.helper.PatternMatchFilter;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.RetainingChooser;
import pcgen.core.EquipmentModifier;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public final class ChooseLoader
{
	private ChooseLoader()
	{
		// Utility Class, no construction needed
	}

	public static boolean isEqModChooseToken(String key)
	{
		return TokenStore.inst().getTokenMap(EqModChooseLstToken.class)
				.get(key) != null;
		// || isGlobalChooseToken(key);
	}

	public static boolean isGlobalChooseToken(String key)
	{
		return TokenStore.inst().getTokenMap(ChooseLstToken.class).get(key) != null;
	}

	public static boolean parseEqModToken(EquipmentModifier mod, String prefix,
			String key, String value)
	{
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				EqModChooseLstToken.class);
		EqModChooseLstToken token = (EqModChooseLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, mod, value);
			if (!token.parse(mod, prefix, value))
			{
				Logging.deprecationPrint("Error parsing CHOOSE: " + key + ":"
						+ value + " in " + mod.getDisplayName() + " of "
						+ mod.getSourceURI());
				return false;
			}
			return true;
		}
		// in case global use is needed:
		// parseToken(mod, prefix, key, value, -9);
		// Always have to return true to maintain old format as ok, but
		// deprecated
		return true;
	}

	/**
	 * This method is static so it can be used by the ADD Token.
	 * 
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseToken(PObject target, String prefix, String key,
			String value, int level)
	{
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				ChooseLstToken.class);
		ChooseLstToken token = (ChooseLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, prefix, value))
			{
				Logging.deprecationPrint("Error parsing CHOOSE: " + key + "|"
						+ value + " in " + target.getDisplayName() + " of "
						+ target.getSourceURI());
				return false;
			}
			return true;
		}
		else
		{
			Logging.deprecationPrint("Error parsing CHOOSE, invalid SubToken: "
					+ key + " in " + target.getDisplayName() + " of "
					+ target.getSourceURI());
			return false;
		}
	}

	public static PrimitiveChoiceSet<?> parseToken(LoadContext context,
			CDOMObject obj, String key, String value)
			throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				ChooseCDOMLstToken.class);
		ChooseCDOMLstToken token = (ChooseCDOMLstToken) tokenMap.get(key);

		PrimitiveChoiceSet<?> chooser;
		if (token == null)
		{
			chooser = processCompatible(context, obj, key, value);
			if (chooser == null)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Illegal CHOOSE:"
						+ key + " ... '" + value + "'");
				return null;
			}
		}
		else
		{
			LstUtils.deprecationCheck(token, obj, value);
			chooser = token.parse(context, obj, value);
			if (chooser == null)
			{
				chooser = processCompatible(context, obj, key, value);
				if (chooser == null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Error parsing CHOOSE:" + key + " in "
									+ obj.getDisplayName() + ": \"" + value
									+ "\"");
					return null;
				}
			}
		}
		return chooser;
	}

	public static <T extends PObject> PrimitiveChoiceSet<T> parseToken(
			LoadContext context, Class<T> poClass, String value)
	{
		// PC[TYPE=x|<primitive1>|<primitive2>|<primitive3>]|QUALIFIED[!TYPE=y,TYPE=z|<primitive4>]
		if (value.charAt(0) == '|')
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"CHOOSE arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"CHOOSE arguments may not end with | : " + value);
		}
		if (value.indexOf("||") != -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"CHOOSE arguments uses double separator || : " + value);
		}
		List<PrimitiveChoiceFilter<T>> pcfList = new ArrayList<PrimitiveChoiceFilter<T>>();
		List<PrimitiveChoiceSet<T>> pcsList = new ArrayList<PrimitiveChoiceSet<T>>();

		StringBuilder remainingValue = new StringBuilder(value);
		while (remainingValue.length() > 0)
		{
			int pipeLoc = remainingValue.indexOf("|");
			int openBracketLoc = remainingValue.indexOf("[");
			if (pipeLoc == -1 && openBracketLoc == -1)
			{
				// Could be a primitive or a Qualifier...
				String key = remainingValue.toString();
				PrimitiveChoiceSet<T> qual = getQualifier(context, poClass,
						key, null);
				if (qual == null)
				{
					PrimitiveChoiceFilter<T> pcf = ChooseLoader
							.getPrimitiveChoiceFilter(context, poClass, key);
					// TODO Error if null
					if (pcf != null)
					{
						pcfList.add(pcf);
					}
				}
				else
				{
					pcsList.add(qual);
				}
				remainingValue.setLength(0);
			}
			else if (openBracketLoc == -1 || pipeLoc > 0
					&& pipeLoc < openBracketLoc)
			{
				// Still could be a primitive or a Qualifier...
				String key = remainingValue.substring(0, pipeLoc);
				PrimitiveChoiceSet<T> qual = getQualifier(context, poClass,
						key, null);
				if (qual == null)
				{
					PrimitiveChoiceFilter<T> pcf = ChooseLoader
							.getPrimitiveChoiceFilter(context, poClass, key);
					pcfList.add(pcf);
				}
				else
				{
					pcsList.add(qual);
				}
				remainingValue.delete(0, pipeLoc + 1);
			}
			else
			{
				// bracket before pipe :)
				int closeBracketLoc = remainingValue.indexOf("]");
				if (remainingValue.lastIndexOf("[", closeBracketLoc) != openBracketLoc)
				{
					// TODO Error (two open brackets)
					throw new IllegalStateException();
				}
				String key = remainingValue.substring(0, openBracketLoc);
				String args = remainingValue.substring(openBracketLoc + 1,
						remainingValue.length() - 1);
				if (closeBracketLoc == remainingValue.length() - 1)
				{
					remainingValue.setLength(0);
				}
				else if (remainingValue.charAt(closeBracketLoc + 1) != '|')
				{
					// TODO Error ... ] must be followed by |
					throw new IllegalStateException();
				}
				else
				{
					remainingValue.delete(0, closeBracketLoc + 1);
				}
				PrimitiveChoiceSet<T> qual = getQualifier(context, poClass,
						key, args);
				if (qual == null)
				{
					// TODO Error bracketed item must be a qualifier
					throw new IllegalStateException();
				}
				pcsList.add(qual);
			}
		}
		if (!pcfList.isEmpty())
		{
			RetainingChooser<T> fc = new RetainingChooser<T>(poClass);
			fc.addAllRetainingChoiceFilters(pcfList);
			pcsList.add(fc);
		}
		if (pcsList.isEmpty())
		{
			// Error - no choices - how??
			throw new IllegalArgumentException();
		}
		else if (pcsList.size() == 1)
		{
			return pcsList.get(0);
		}
		else
		{
			return new CompoundOrChoiceSet<T>(pcsList);
		}
	}

	public static <T extends PObject> PrimitiveChoiceSet<T> getQualifier(
			LoadContext context, Class<T> cl, String key, String value)
	{
		TokenStore ts = TokenStore.inst();
		int equalLoc = key.indexOf('=');
		String condition = null;
		if (equalLoc != -1)
		{
			condition = key.substring(equalLoc + 1);
			key = key.substring(0, equalLoc);
		}
		ChooseLstQualifierToken<T> qual = ts.getChooseQualifier(cl, key);
		if (qual == null)
		{
			ChooseLstGlobalQualifierToken<T> potoken = ts
					.getGlobalChooseQualifier(key);
			if (potoken == null)
			{
				return null;
			}
			potoken.initialize(context, cl, condition, value);
			return potoken;
		}
		else
		{
			if (!qual.initialize(context, cl, condition, value))
			{
				return null;
			}
			return qual;
		}
	}

	public static <T extends PObject> PrimitiveChoiceFilter<T> getPrimitiveChoiceFilter(
			LoadContext context, Class<T> cl, String key)
	{
		if (key.indexOf(',') == -1)
		{
			return getAtomicChoiceFilter(context, cl, key);
		}
		StringTokenizer st = new StringTokenizer(key, ",");
		List<PrimitiveChoiceFilter<T>> filterList = new ArrayList<PrimitiveChoiceFilter<T>>();
		while (st.hasMoreTokens())
		{
			filterList.add(getAtomicChoiceFilter(context, cl, st.nextToken()));
		}
		return new CompoundAndFilter<T>(filterList);
	}

	public static <T extends PObject> PrimitiveChoiceFilter<T> getAtomicChoiceFilter(
			LoadContext context, Class<T> cl, String key)
	{
		int openBracketLoc = key.indexOf('[');
		int closeBracketLoc = key.indexOf(']');
		int equalLoc = key.indexOf('=');
		String tokKey;
		String tokValue;
		String tokRestriction;
		if (openBracketLoc == -1)
		{
			if (closeBracketLoc != -1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " has a close bracket but no open bracket");
				return null;
			}
			if (equalLoc == -1)
			{
				tokKey = key;
				tokValue = null;
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1);
			}
			tokRestriction = null;
		}
		else
		{
			if (closeBracketLoc == -1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " has an open bracket but no close bracket");
				return null;
			}
			if (closeBracketLoc != key.length() - 1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " had close bracket, but had characters "
						+ "following the close bracket");
				return null;
			}
			if (equalLoc == -1)
			{
				tokKey = key.substring(0, openBracketLoc);
				tokValue = null;
				tokRestriction = key.substring(openBracketLoc + 1, closeBracketLoc);
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1, openBracketLoc);
				tokRestriction = key.substring(openBracketLoc + 1, closeBracketLoc);
			}
		}
		PrimitiveToken<T> prim = TokenStore.inst().getPrimitive(cl, tokKey);
		if (prim == null)
		{
			if (key.startsWith(Constants.LST_TYPE_OLD)
					|| key.startsWith(Constants.LST_TYPE))
			{
				return TokenUtilities.getTypeReference(context, cl, key
						.substring(5));
			}
			else if (key.startsWith(Constants.LST_NOT_TYPE_OLD)
					|| key.startsWith(Constants.LST_NOT_TYPE))
			{
				return new NegatingFilter<T>(TokenUtilities.getTypeReference(
						context, cl, key.substring(6)));
			}
			else if (key.indexOf('%') != -1)
			{
				return new PatternMatchFilter<T>(cl, key);
			}
			else
			{
				return context.ref.getCDOMReference(cl, key);
			}
		}
		else
		{
			if (!prim.initialize(context, tokValue, tokRestriction))
			{
				return null;
			}
		}
		return prim;
	}

	private static final ReverseIntegerComparator REVERSE = new ReverseIntegerComparator();

	private static PrimitiveChoiceSet<?> processCompatible(LoadContext context,
			CDOMObject pobj, String key, String value)
	{
		Collection<ChooseCompatibilityToken> tokens = TokenStore.inst()
				.getCompatibilityToken(ChooseCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, ChooseCompatibilityToken> tkm = new TripleKeyMap<Integer, Integer, Integer, ChooseCompatibilityToken>();
			for (ChooseCompatibilityToken tok : tokens)
			{
				tkm.put(Integer.valueOf(tok.compatibilityLevel()), Integer
						.valueOf(tok.compatibilitySubLevel()), Integer
						.valueOf(tok.compatibilityPriority()), tok);
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
						ChooseCompatibilityToken tok = tkm.get(level, subLevel,
								priority);
						try
						{
							PrimitiveChoiceSet<?> parse = tok.parse(context,
									pobj, value);
							if (parse != null)
							{
								return parse;
							}
						}
						catch (PersistenceLayerException e)
						{
							Logging.addParseMessage(Logging.LST_ERROR,
									"Error parsing CHOOSE Token '" + key
											+ "' for " + pobj.getDisplayName());
						}
					}
					tertiarySet.clear();
				}
				secondarySet.clear();
			}
		}
		return null;
	}

	public static PrimitiveChoiceSet<?>[] parseEqModToken(LoadContext context,
			EquipmentModifier mod, String key, String value)
			throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				ChooseCDOMLstToken.class);
		ChooseCDOMLstToken token = (ChooseCDOMLstToken) tokenMap.get(key);

		PrimitiveChoiceSet<?>[] chooser;
		/*
		 * TODO For now, don't process the globals - too hard with CHOOSE:NUMBER
		 */
		// if (token == null)
		// {
		chooser = processEqModCompatible(context, mod, key, value);
		if (chooser == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Illegal CHOOSE:" + key
					+ " ... '" + value + "'");
			return null;
		}
		// }
		// else
		// {
		// LstUtils.deprecationCheck(token, mod, value);
		// chooser = token.parse(context, mod, value);
		// if (chooser == null)
		// {
		// chooser = processEqModCompatible(context, mod, key, value);
		// if (chooser == null)
		// {
		// Logging.addParseMessage(Logging.LST_ERROR,
		// "Error parsing CHOOSE:" + key + " in "
		// + mod.getDisplayName() + ": \"" + value
		// + "\"");
		// return null;
		// }
		// }
		// }
		return chooser;
	}

	private static PrimitiveChoiceSet<?>[] processEqModCompatible(
			LoadContext context, EquipmentModifier pobj, String key,
			String value)
	{
		Collection<EqModChooseCompatibilityToken> tokens = TokenStore
				.inst()
				.getCompatibilityToken(EqModChooseCompatibilityToken.class, key);
		if (tokens != null && !tokens.isEmpty())
		{
			TripleKeyMap<Integer, Integer, Integer, EqModChooseCompatibilityToken> tkm = new TripleKeyMap<Integer, Integer, Integer, EqModChooseCompatibilityToken>();
			for (EqModChooseCompatibilityToken tok : tokens)
			{
				tkm.put(Integer.valueOf(tok.compatibilityLevel()), Integer
						.valueOf(tok.compatibilitySubLevel()), Integer
						.valueOf(tok.compatibilityPriority()), tok);
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
						EqModChooseCompatibilityToken tok = tkm.get(level,
								subLevel, priority);
						try
						{
							PrimitiveChoiceSet<?>[] parse = tok.parse(context,
									pobj, value);
							if (parse != null)
							{
								return parse;
							}
						}
						catch (PersistenceLayerException e)
						{
							Logging.addParseMessage(Logging.LST_ERROR,
									"Error parsing CHOOSE Token '" + key
											+ "' for " + pobj.getDisplayName());
						}
					}
					tertiarySet.clear();
				}
				secondarySet.clear();
			}
		}
		return null;
	}

}
