/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.GrantFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Ability;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken extends AbstractToken implements RaceLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "STARTFEATS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append("FEAT|POOL|").append(Integer.parseInt(value));

			final BonusObj bon = Bonus.newBonus(sb.toString());
			final PreParserFactory factory = PreParserFactory.getInstance();
			final StringBuffer buf = new StringBuffer();

			buf.append("PREMULT:1,[PREHD:1+],[PRELEVEL:1]");

			final Prerequisite prereq = factory.parse(buf.toString());
			bon.addPreReq(prereq);

			race.setBonusInitialFeats(bon);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Number in " + getTokenName()
					+ " must be greater than zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}

		CDOMGroupRef<Ability> ref =
				context.ref.getCDOMAllReference(ABILITY_CLASS,
					AbilityCategory.FEAT);
		ReferenceChoiceSet<Ability> rcs =
				new ReferenceChoiceSet<Ability>(Collections.singletonList(ref));
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>(getTokenName(), rcs);
		PCGraphGrantsEdge edge =
				context.getGraphContext().grant(getTokenName(), race, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(featCount));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(featCount));
		GrantFactory<Ability> gf = new GrantFactory<Ability>(edge);
		/*
		 * FUTURE Technically, this Category item should not be in the
		 * GrantFactory, as it really belogs as something that can be extracted
		 * from the ChoiceSet...
		 */
		gf
			.setAssociation(AssociationKey.ABILITY_CATEGORY,
				AbilityCategory.FEAT);
		gf.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		PCGraphGrantsEdge gfedge =
				context.getGraphContext().grant(getTokenName(), race, gf);
		/*
		 * This prereq exists solely to prevent the ability to select Feats
		 * before first level. It simply aligns this to the rules that provide
		 * for the extra feats for a starting character (which is first level)
		 * This was established through questioning of the more senior code
		 * monkeys on one of the lists in early 2007 - Tom Parker Mar/28/2007
		 */
		Prerequisite prereq =
				getPrerequisite("PREMULT:1,[PRELEVEL:1],[PREHD:1+]");
		gfedge.addPreReq(prereq);
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		GraphChanges<ChoiceSet> choiceChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					race, ChoiceSet.class);
		if (choiceChanges == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				choiceChanges.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		GraphChanges<GrantFactory> grantChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					race, GrantFactory.class);
		Collection<LSTWriteable> grantAdded = grantChanges.getAdded();
		if (grantAdded == null || grantAdded.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : mtl.getKeySet())
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat = null;
				AbilityCategory cat = null;
				for (LSTWriteable gw : grantAdded)
				{
					GrantFactory<?> gf = (GrantFactory<?>) gw;
					if (gf.usesChoiceSet(cs))
					{
						cat =
								gf
									.getAssociation(AssociationKey.ABILITY_CATEGORY);
						nat = gf.getAssociation(AssociationKey.ABILITY_NATURE);
						break;
					}
				}
				if (nat == null)
				{
					context
						.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				if (cat == null)
				{
					context
						.addWriteMessage("Unable to find Category for GrantFactory");
					return null;
				}
				if (!AbilityCategory.FEAT.equals(cat)
					|| !AbilityNature.NORMAL.equals(nat))
				{
					// can't handle those here!
					continue;
				}
				List<AssociatedPrereqObject> assocList = mtl.getListFor(lstw);
				if (assocList.size() != 1)
				{
					context
						.addWriteMessage("Only one Association to a CHOOSE can be made per object");
					return null;
				}
				AssociatedPrereqObject assoc = assocList.get(0);
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getTokenName()
						+ " Count");
					return null;
				}
				addStrings.add(f.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
