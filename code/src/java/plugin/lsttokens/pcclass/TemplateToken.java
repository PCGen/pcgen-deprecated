package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with TEMPLATE Token
 */
public class TemplateToken implements PCClassLstToken, PCClassUniversalLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.addTemplate(level, value);
		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
	{
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			String substring = value.substring(Constants.LST_CHOOSE.length());
			if (substring.length() == 0)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ":"
					+ Constants.LST_CHOOSE);
				Logging.errorPrint("  Requires at least one argument");
				return false;
			}
			if (substring.charAt(0) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | , see: " + value);
				return false;
			}
			if (substring.charAt(substring.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | , see: " + value);
				return false;
			}
			if (substring.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}

			StringTokenizer tok =
					new StringTokenizer(substring, Constants.PIPE);

			ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
					new ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>(1, tok
						.countTokens());

			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				CDOMSimpleSingleRef<PCTemplate> ref =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText);
				cl.addChoice(ref);
			}
			context.graph.linkObjectIntoGraph(getTokenName(), po, cl);
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			String substring =
					value.substring(Constants.LST_ADDCHOICE.length());
			if (substring.length() == 0)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ":"
					+ Constants.LST_CHOOSE);
				Logging.errorPrint("  Requires at least one argument");
				return false;
			}

			// FIXME Need to handle this :)
			/*
			 * The disappointing thing here is that this produces interaction
			 * with my idea about .MODs and .CLEARs, since this is an implicit
			 * .MOD, but .MODs something that the context will not be aware is
			 * being modified :/
			 */
		}
		else
		{
			if (value.length() == 0)
			{
				Logging.errorPrint(getTokenName()
					+ " may not have empty argument");
				return false;
			}
			if (value.charAt(0) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | , see: " + value);
				return false;
			}
			if (value.charAt(value.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | , see: " + value);
				return false;
			}
			if (value.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}

			StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				CDOMSimpleSingleRef<PCTemplate> ref =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText);
				context.graph.linkObjectIntoGraph(getTokenName(), po, ref);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, PObject po)
	{
		Set<PCGraphEdge> directEdges =
				context.graph.getChildLinksFromToken(getTokenName(), po,
					PCTEMPLATE_CLASS);
		Set<PCGraphEdge> choiceEdges =
				context.graph.getChildLinksFromToken(getTokenName(), po,
					ChoiceSet.class);
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		int currentIndex = 0;
		int choiceSize = choiceEdges == null ? 0 : choiceEdges.size();
		int directSize =
				directEdges == null ? 0 : directEdges.isEmpty() ? 0 : 1;
		if ((directSize + choiceSize) == 0)
		{
			// No templates
			return null;
		}
		String[] array = new String[directSize + choiceSize];
		if (directEdges != null && !directEdges.isEmpty())
		{
			for (PCGraphEdge edge : directEdges)
			{
				set.add((CDOMReference<?>) edge.getSinkNodes().get(0));
			}

			array[currentIndex++] =
					ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
		}
		if (choiceEdges != null && !choiceEdges.isEmpty())
		{
			for (PCGraphEdge edge : choiceEdges)
			{
				ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
						(ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>) edge
							.getSinkNodes().get(0);
				set.clear();
				set.addAll(cl.getSet());

				array[currentIndex++] =
						(Constants.LST_CHOOSE + ReferenceUtilities
							.joinLstFormat(set, Constants.PIPE));
			}
		}
		return array;
	}

}
