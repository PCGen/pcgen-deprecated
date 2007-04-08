package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCClassLstToken, PCClassClassLstToken
{
	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			pcclass.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		return parseWeaponBonus(context, pcc, value);
	}

	public boolean parseWeaponBonus(LoadContext context, CDOMObject obj,
		String value)
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

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		ChoiceSet<CDOMReference<WeaponProf>> cl =
				new ChoiceSet<CDOMReference<WeaponProf>>(1, tok.countTokens());
		while (tok.hasMoreTokens())
		{
			CDOMReference<WeaponProf> ref =
					TokenUtilities.getTypeOrPrimitive(context,
						WEAPONPROF_CLASS, tok.nextToken());
			if (ref == null)
			{
				return false;
			}
			cl.addChoice(ref);
		}
		context.graph.linkObjectIntoGraph(getTokenName(), obj, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Set<PCGraphEdge> choiceEdges =
				context.graph.getChildLinksFromToken(getTokenName(), pcc,
					ChoiceSet.class);
		if (choiceEdges == null || choiceEdges.isEmpty())
		{
			return null;
		}
		if (choiceEdges.size() > 1)
		{
			context.addWriteMessage(getTokenName()
				+ " may only have one ChoiceSet linked in the Graph");
			return null;
		}
		Set<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		PCGraphEdge edge = choiceEdges.iterator().next();
		set.addAll(((ChoiceSet<CDOMReference<?>>) edge.getSinkNodes().get(0))
			.getSet());
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.PIPE)};
	}
}
