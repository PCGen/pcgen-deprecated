/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.pcclass;

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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.PCClassUniversalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.FeatParser;
import pcgen.util.Logging;

/**
 * Class deals with VFEAT Token
 */
public class VfeatToken extends AbstractToken implements PCClassLstToken,
		PCClassUniversalLstToken
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		List<QualifiedObject<String>> vfeatList =
				FeatParser.parseVirtualFeatListToQualObj(value);
		for (final QualifiedObject<String> ability : vfeatList)
		{
			String preLevelString = "";
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				preLevelString =
						"PRECLASS:1," + pcclass.getKeyName() + "=" + level; //$NON-NLS-1$ //$NON-NLS-2$
				Prerequisite r = factory.parse(preLevelString);
				ability.addPrerequisite(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				Logging.errorPrint("Failed to create level prereq for VFEAT "
					+ value + ". Prereq was " + preLevelString + ".", notUsed);
				return false;
			}
			pcclass.addAbility(AbilityCategory.FEAT, Ability.Nature.VIRTUAL,
				ability);
		}
		pcclass.addVirtualFeats(level, FeatParser.parseVirtualFeatList(value));
		return true;
	}

	public boolean parse(LoadContext context, PObject po, String value)
	{
		return parseFeat(context, po, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
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
						pcgen.cdom.enumeration.AbilityCategory.FEAT, token);
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

	public String[] unparse(LoadContext context, PObject pct)
	{
		AssociatedChanges<Ability> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pct, ABILITY_CLASS);
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
						+ getTokenName() + " must be of VIRTUAL AbilityNature");
					return null;
				}
				if (!pcgen.cdom.enumeration.AbilityCategory.FEAT
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
