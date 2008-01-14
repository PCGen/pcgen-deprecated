/*
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
 */
package plugin.lsttokens.auto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;

public class FeatToken extends AbstractToken implements AutoLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(PObject target, String value, int level)
	{
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		
		tok.nextToken(); // Throw away FEAT

		ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
		if (level > -9)
		{
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				String preLevelString = "PRELEVEL:MIN=" + level; //$NON-NLS-1$
				if (target instanceof PCClass)
				{
					// Classes handle this differently
					preLevelString =
							"PRECLASS:1," + target.getKeyName() + "=" + level; //$NON-NLS-1$ //$NON-NLS-2$
				}
				Prerequisite r = factory.parse(preLevelString);
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				return false;
			}
		}
		while (tok.hasMoreTokens())
		{
			String feat = tok.nextToken();
			if (feat.startsWith(".CLEAR."))
			{
				List<QualifiedObject<String>> ao =
						target.getRawAbilityObjects(
							pcgen.core.AbilityCategory.FEAT,
							Ability.Nature.AUTOMATIC);
				for (QualifiedObject<String> qo : ao)
				{
					if (qo instanceof QualifiedObject.AutoQualifiedObject)
					{
						String name = feat.substring(7);
						if (name.equalsIgnoreCase(qo.getObject(null))
							&& preReqs.equals(qo.getPrereqs()))
						{
							target.removeAbility(
								pcgen.core.AbilityCategory.FEAT,
								Ability.Nature.AUTOMATIC, qo);
						}
					}
				}
			}
			else
			{
				target.addAbility(pcgen.core.AbilityCategory.FEAT,
					Ability.Nature.AUTOMATIC,
					new QualifiedObject.AutoQualifiedObject<String>(feat,
						preReqs));
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PObject obj, String value)
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
			String feat = tok.nextToken();
			if (feat.startsWith(".CLEAR."))
			{
				String name = feat.substring(7);
				CDOMCategorizedSingleRef<Ability> ability =
						context.ref.getCDOMReference(ABILITY_CLASS, ac, name);
				context.getGraphContext().remove(getTokenName(), obj, ability);
			}
			else
			{
				CDOMCategorizedSingleRef<Ability> ability =
						context.ref.getCDOMReference(ABILITY_CLASS, ac, feat);
				AssociatedPrereqObject edge =
						context.getGraphContext().grant(getTokenName(), obj,
							ability);
				edge.setAssociation(AssociationKey.ABILITY_NATURE, an);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		AssociatedChanges<Ability> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, ABILITY_CLASS);
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
				if (!AbilityNature.AUTOMATIC.equals(an))
				{
					context.addWriteMessage("Abilities awarded by "
						+ getTokenName()
						+ " must be of AUTOMATIC AbilityNature");
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
