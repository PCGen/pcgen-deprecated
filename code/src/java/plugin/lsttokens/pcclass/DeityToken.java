package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "DEITY";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.clearDeityList();

		StringTokenizer st = new StringTokenizer(Constants.PIPE);
		while (st.hasMoreTokens())
		{
			pcclass.addDeity(st.nextToken());
		}
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Deity> deity =
					context.ref.getCDOMReference(Deity.class, tokText);
			context.graph.linkObjectIntoGraph(getTokenName(), pcc, deity);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pcc,
					Deity.class);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<Deity>) edge.getSinkNodes().get(0));
		}
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.PIPE)};
	}
}
