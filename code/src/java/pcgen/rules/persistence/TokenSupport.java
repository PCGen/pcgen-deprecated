package pcgen.rules.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.CompoundAndFilter;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.NegatingFilter;
import pcgen.cdom.helper.PatternMatchFilter;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.RetainingChooser;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.ChooseTokenIterator;
import pcgen.rules.persistence.TokenLibrary.PreTokenIterator;
import pcgen.rules.persistence.TokenLibrary.SubTokenIterator;
import pcgen.rules.persistence.TokenLibrary.TokenIterator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ChoiceSetToken;
import pcgen.rules.persistence.token.ChooseLstGlobalQualifierToken;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.util.TokenFamilyIterator;
import pcgen.rules.persistence.util.TokenFamilySubIterator;
import pcgen.util.Logging;

public class TokenSupport
{
	public static final Class<CDOMObject> CDOM_OBJECT_CLASS = CDOMObject.class;

	public static <T extends CDOMObject> boolean processToken(LoadContext context,
			T derivative, String typeStr, String argument)
			throws PersistenceLayerException
	{
		Class<T> cl = (Class<T>) derivative.getClass();
		for (Iterator<? extends CDOMToken<T>> it = new TokenIterator<T, CDOMToken<T>>(
				cl, typeStr); it.hasNext();)
		{
			CDOMToken<T> token = it.next();
			if (token.parse(context, derivative, argument))
			{
				return true;
			}
			Logging.addParseMessage(Logging.LST_INFO,
					"Failed in parsing typeStr: " + token);
		}
		Logging.errorPrint("Illegal Token '" + typeStr + "' '" + argument
				+ "' for " + cl.getName() + " " + derivative.getDisplayName());
		return false;
	}

	public static <T extends CDOMObject> PrimitiveChoiceSet<T> getChoiceSet(
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
					PrimitiveChoiceFilter<T> pcf = getPrimitiveChoiceFilter(
							context, poClass, key);
					if (pcf == null)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"Choice argument was not valid : " + value);
						return null;
					}
					else
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
					PrimitiveChoiceFilter<T> pcf = getPrimitiveChoiceFilter(
							context, poClass, key);
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
					Logging.errorPrint("Found two Open Brackets in Choice: "
							+ value);
					return null;
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
					Logging.errorPrint("Close Bracket was not "
							+ "followed by the end or by a pipe: " + value);
					return null;
				}
				else
				{
					remainingValue.delete(0, closeBracketLoc + 1);
				}
				PrimitiveChoiceSet<T> qual = getQualifier(context, poClass,
						key, args);
				if (qual == null)
				{
					Logging.errorPrint("Unable to Get Choice Qualifier, "
							+ "input was invalid: " + value);
					return null;
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
			Logging.errorPrint("Choice resulted in no choices from input: "
					+ value);
			return null;
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

	public static <T extends CDOMObject> PrimitiveChoiceFilter<T> getPrimitiveChoiceFilter(
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

	public static <T extends CDOMObject> PrimitiveChoiceSet<T> getQualifier(
			LoadContext context, Class<T> cl, String key, String value)
	{
		/*
		 * TODO This splits TYPE= before it goes into getGlobalChooseQualifier !
		 */
		int equalLoc = key.indexOf('=');
		String condition = null;
		if (equalLoc != -1)
		{
			condition = key.substring(equalLoc + 1);
			key = key.substring(0, equalLoc);
		}
		ChooseLstQualifierToken<T> qual = TokenLibrary.getChooseQualifier(cl,
				key);
		if (qual == null)
		{
			ChooseLstGlobalQualifierToken<T> potoken = TokenLibrary
					.getGlobalChooseQualifier(key);
			if (potoken == null)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Invalid Qualifier: " + cl + " " + key + " " + value);
				return null;
			}
			potoken.initialize(context, cl, condition, value);
			return potoken;
		}
		else
		{
			if (!qual.initialize(context, cl, condition, value))
			{
				Logging.errorPrint("Failed to Initialize Qualifier: " + cl
						+ " " + key + " " + value);
				return null;
			}
			return qual;
		}
	}

	public static <T extends CDOMObject> PrimitiveChoiceFilter<T> getAtomicChoiceFilter(
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
				tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1, openBracketLoc);
				tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
		}
		PrimitiveToken<T> prim = TokenLibrary.getPrimitive(cl, tokKey);
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

	public static <T> boolean processSubToken(LoadContext context, T cdo,
			String tokenName, String key, String value)
			throws PersistenceLayerException
	{
		for (Iterator<CDOMSubToken<T>> it = new SubTokenIterator<T, CDOMSubToken<T>>(
				(Class<T>) cdo.getClass(), tokenName, key); it.hasNext();)
		{
			CDOMSubToken<T> token = it.next();
			if (token.parse(context, cdo, value))
			{
				return true;
			}
			Logging.addParseMessage(Logging.LST_INFO,
					"Failed in parsing typeStr: " + token);
		}
		/*
		 * CONSIDER Better option than toString, given that T != CDOMObject
		 */
		Logging.errorPrint("Illegal " + tokenName + " subtoken '" + key + "' '"
				+ value + "' for " + cdo.toString());
		return false;
	}

	public static <T extends CDOMObject> String[] unparse(LoadContext context, T cdo,
			String tokenName)
	{
		Set<String> set = new TreeSet<String>();
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilySubIterator<T> it = new TokenFamilySubIterator<T>(cl, tokenName);
		while (it.hasNext())
		{
			CDOMSecondaryToken<? super T> token = it.next();
			String[] s = token.unparse(context, cdo);
			if (s != null)
			{
				for (String aString : s)
				{
					set.add(token.getTokenName() + '|' + aString);
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public static <T extends CDOMObject> Collection<String> unparse(
			LoadContext context, T cdo)
	{
		Set<String> set = new TreeSet<String>();
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilyIterator<T> it = new TokenFamilyIterator<T>(cl);
		while (it.hasNext())
		{
			CDOMPrimaryToken<? super T> token = it.next();
			String[] s = token.unparse(context, cdo);
			if (s != null)
			{
				for (String aString : s)
				{
					set.add(token.getTokenName() + ':' + aString);
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public static <T extends CDOMObject> PrimitiveChoiceSet<?> getChoiceSet(
			LoadContext context, T obj, String key, String val)
			throws PersistenceLayerException
	{
		for (Iterator<ChoiceSetToken<?>> it = new ChooseTokenIterator<CDOMObject>(
				(Class) obj.getClass(), key); it.hasNext();)
		{
			ChoiceSetToken token = it.next();
			PrimitiveChoiceSet<?> pcs = token.parse(context, obj, val);
			if (pcs == null)
			{
				Logging.addParseMessage(Logging.LST_INFO,
						"Failed in parsing typeStr: " + token);
			}
			return pcs;
		}
		Class<? extends CDOMObject> cl = obj.getClass();
		Logging.addParseMessage(Logging.LST_ERROR, "Illegal Choice Token '"
				+ key + "' '" + val + "' for " + cl.getName() + " "
				+ obj.getDisplayName());
		return null;
	}

	public static Prerequisite getPrerequisite(LoadContext context, String key,
			String value) throws PersistenceLayerException
	{
		for (Iterator<PrerequisiteParserInterface> it = new PreTokenIterator(
				key); it.hasNext();)
		{
			PrerequisiteParserInterface token = it.next();
			Prerequisite p = token.parse(key, value, false, false);
			if (p == null)
			{
				Logging.addParseMessage(Logging.LST_INFO,
						"Failed in parsing Prereq: " + key + " " + value);
			}
			return p;
		}
		Logging.addParseMessage(Logging.LST_ERROR, "Illegal Choice Token '"
				+ key + "' '" + value + "'");
		return null;
	}
}
