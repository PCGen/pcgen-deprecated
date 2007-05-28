package plugin.lsttokens;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.FeatParser;
import pcgen.persistence.lst.utils.TokenUtilities;

public class VFeatLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(PObject obj, String value, int anInt)
		throws PersistenceLayerException
	{
		obj.addVirtualFeats(FeatParser.parseVirtualFeatList(value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		AbilityCategory ac = AbilityCategory.FEAT;
		AbilityNature an = AbilityNature.AUTOMATIC;

		while (tok.hasMoreTokens())
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS, ac, tok
						.nextToken());
			PCGraphGrantsEdge edge =
					context.graph.grant(getTokenName(), obj, ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE, an);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		GraphChanges<Ability> changes =
				context.graph.getChangesFromToken(getTokenName(), obj,
					ABILITY_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		HashMapToList<Set<Prerequisite>, LSTWriteable> m =
				new HashMapToList<Set<Prerequisite>, LSTWriteable>();
		for (LSTWriteable ab : added)
		{
			AssociatedPrereqObject assoc = changes.getAddedAssociation(ab);
			AbilityNature an =
					assoc.getAssociation(AssociationKey.ABILITY_NATURE);
			if (!AbilityNature.AUTOMATIC.equals(an))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of AUTOMATIC AbilityNature");
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

		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<LSTWriteable> abilities = m.getListFor(prereqs);
			set.clear();
			set.addAll(abilities);
			String ab = ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				TreeSet<String> prereqSet = new TreeSet<String>();
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					prereqSet.add(swriter.toString());
				}
				ab =
						ab + Constants.PIPE
							+ StringUtil.join(prereqSet, Constants.PIPE);
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}

}
