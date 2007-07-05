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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.CompoundAndFilter;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.NegatingFilter;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.RetainingChooser;
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
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ChooseLstToken.class);
		ChooseLstToken token = (ChooseLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, prefix, value))
			{
				// 514 deprecation changes
				// Logging
				// .errorPrint("Error parsing CHOOSE: " + key + ":" + value);
				return false;
			}
			return true;
		}
		else
		{
			// 514 deprecation changes
			// Logging
			// .errorPrint("Error parsing CHOOSE, invalid SubToken: " + key);
			return false;
		}
	}

	public static PrimitiveChoiceSet<?> parseToken(LoadContext context,
		CDOMObject obj, String key, String value)
		throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ChooseCDOMLstToken.class);
		ChooseCDOMLstToken token = (ChooseCDOMLstToken) tokenMap.get(key);

		if (token != null)
		{
			LstUtils.deprecationCheck(token, obj, value);
			PrimitiveChoiceSet<?> chooser = token.parse(context, obj, value);
			if (chooser == null)
			{
				Logging.errorPrint("Error parsing CHOOSE in "
					+ obj.getDisplayName() + ": \"" + value + "\"");
				return null;
			}
			return chooser;
		}
		else
		{
			Logging.errorPrint("Illegal CHOOSE info '" + value + "'");
			return null;
		}
	}

	public static <T extends PObject> PrimitiveChoiceSet<T> parseToken(
		LoadContext context, Class<T> poClass, String value)
	{
		// PC[TYPE=x|<primitive1>|<primitive2>|<primitive3>]|QUALIFIED[!TYPE=y,TYPE=z|<primitive4>]
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE arguments may not start with | : "
				+ value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging
				.errorPrint("CHOOSE arguments may not end with | : " + value);
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE arguments uses double separator || : "
				+ value);
		}
		List<PrimitiveChoiceFilter<T>> pcfList =
				new ArrayList<PrimitiveChoiceFilter<T>>();
		List<PrimitiveChoiceSet<T>> pcsList =
				new ArrayList<PrimitiveChoiceSet<T>>();

		StringBuilder remainingValue = new StringBuilder(value);
		while (remainingValue.length() > 0)
		{
			int pipeLoc = remainingValue.indexOf("|");
			int openBracketLoc = remainingValue.indexOf("[");
			if (pipeLoc > 0 && pipeLoc < openBracketLoc)
			{
				PrimitiveChoiceFilter<T> pcf =
						ChooseLoader.getPrimitiveChoiceFilter(context, poClass,
							remainingValue.substring(0, pipeLoc));
				pcfList.add(pcf);
				remainingValue.delete(0, pipeLoc);
			}
			else
			{
				int closeBracketLoc = remainingValue.indexOf("]");
				if (remainingValue.lastIndexOf("[", closeBracketLoc) != openBracketLoc)
				{
					// TODO Error (two open brackets)
					throw new IllegalStateException();
				}
				String key = remainingValue.substring(0, openBracketLoc);
				String args =
						remainingValue.substring(openBracketLoc + 1,
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
				TokenStore ts = TokenStore.inst();
				ChooseLstQualifierToken<T> qual =
						ts.getChooseQualifier(poClass, key);
				if (qual == null)
				{
					ChooseLstGlobalQualifierToken<T> potoken =
							ts.getGlobalChooseQualifier(key);
					if (potoken == null)
					{
						// TODO Error (token with brackets not found)
						throw new IllegalStateException();
					}
					potoken.initialize(context, poClass, value);
					pcsList.add(potoken);
				}
				else
				{
					qual.initialize(context, poClass, args);
					pcsList.add(qual);
				}
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

	public static <T extends PObject> PrimitiveChoiceFilter<T> getPrimitiveChoiceFilter(
		LoadContext context, Class<T> cl, String key)
	{
		if (key.indexOf(',') == -1)
		{
			return getAtomicChoiceFilter(context, cl, key);
		}
		StringTokenizer st = new StringTokenizer(key, ",");
		List<PrimitiveChoiceFilter<T>> filterList =
				new ArrayList<PrimitiveChoiceFilter<T>>();
		while (st.hasMoreTokens())
		{
			filterList.add(getAtomicChoiceFilter(context, cl, st.nextToken()));
		}
		return new CompoundAndFilter<T>(filterList);
	}

	public static <T extends PObject> PrimitiveChoiceFilter<T> getAtomicChoiceFilter(
		LoadContext context, Class<T> cl, String key)
	{
		int equalLoc = key.indexOf('=');
		String tokKey;
		String tokValue;
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
		PrimitiveToken<T> prim =
				TokenStore.inst().getPrimitive(cl, tokKey);
		if (prim == null)
		{
			if (tokKey.startsWith(Constants.LST_TYPE_OLD)
				|| tokKey.startsWith(Constants.LST_TYPE))
			{
				return TokenUtilities.getTypeReference(context, cl, tokKey
					.substring(5));
			}
			else if (tokKey.startsWith(Constants.LST_NOT_TYPE_OLD)
				|| tokKey.startsWith(Constants.LST_NOT_TYPE))
			{
				return new NegatingFilter<T>(TokenUtilities.getTypeReference(
					context, cl, tokKey.substring(5)));
			}
			else
			{
				return context.ref.getCDOMReference(cl, tokKey);
			}
		}
		else
		{
			prim.initialize(context, tokValue);
		}
		return prim;
	}
}
