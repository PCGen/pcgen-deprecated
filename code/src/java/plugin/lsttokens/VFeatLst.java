package plugin.lsttokens;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.FeatParser;
import pcgen.util.Logging;

public class VFeatLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.addVirtualFeats(FeatParser.parseVirtualFeatList(value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return false;
		}

		ArrayList<AssociatedPrereqObject> edgeList =
				new ArrayList<AssociatedPrereqObject>();

		while (true)
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS,
						AbilityCategory.FEAT, token);
			AssociatedPrereqObject edge =
					context.getGraphContext().grant(getTokenName(), obj,
						ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.VIRTUAL);
			edgeList.add(edge);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			for (AssociatedPrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		AssociatedChanges<Ability> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					cdo, ABILITY_CLASS);
		if (changes == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				changes.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		MapToList<Set<Prerequisite>, LSTWriteable> m =
				new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (LSTWriteable ab : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
			{
				AbilityNature an =
						assoc.getAssociation(AssociationKey.ABILITY_NATURE);
				if (!AbilityNature.VIRTUAL.equals(an))
				{
					context.addWriteMessage("Abilities awarded by "
						+ getTokenName() + " must be of NORMAL AbilityNature");
					return null;
				}
				if (!AbilityCategory.FEAT
					.equals(((CategorizedCDOMReference<Ability>) ab)
						.getCDOMCategory()))
				{
					context.addWriteMessage("Abilities awarded by "
						+ getTokenName() + " must be of CATEGORY FEAT");
					return null;
				}
				m.addToListFor(new HashSet<Prerequisite>(assoc
					.getPrerequisiteList()), ab);
			}
		}

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String ab =
					ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
						Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				ab =
						ab + Constants.PIPE
							+ getPrerequisiteString(context, prereqs);
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}
}
