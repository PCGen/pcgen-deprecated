package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements PCClassLstToken, PCClassClassLstToken
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setLanguageBonus(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();

			if (Constants.LST_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), pcc,
					LANGUAGE_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Language> lang =
						TokenUtilities.getObjectReference(context,
							LANGUAGE_CLASS, tokText.substring(7));
				if (lang == null)
				{
					return false;
				}
				context.graph.unlinkChildNode(getTokenName(), pcc, lang);
			}
			else
			{
				/*
				 * Note this HAS to be added one-by-one, because the
				 * .unlinkChildNodesOfClass method above does NOT recognize the
				 * Language object and therefore doesn't know how to search the
				 * sublists
				 */
				CDOMReference<Language> lang =
						TokenUtilities.getObjectReference(context,
							LANGUAGE_CLASS, tokText);
				if (lang == null)
				{
					return false;
				}
				/*
				 * BUG FIXME This is NOT A GRANT - it is a ChoiceList like WeaponBonus
				 */
				context.graph.linkObjectIntoGraph(getTokenName(), pcc, lang);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pcc,
					LANGUAGE_CLASS);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<Language>) edge.getSinkNodes().get(0));
		}
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.COMMA)};
	}
}
