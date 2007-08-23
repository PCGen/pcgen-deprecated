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
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.GrantFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS =
			ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "SKILLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		int skillCount = 0;

		if (value.indexOf('|') >= 0)
		{
			try
			{
				skillCount = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Import error: Expected first value of "
					+ "SKILLLIST token with a | to be a number");
				return false;
			}
		}

		final List<String> skillChoices = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			skillChoices.add(aTok.nextToken());
		}

		// Protection against a "" value parameter
		if (skillChoices.size() > 0)
		{
			pcclass.setClassSkillChoices(skillCount, skillChoices);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		int pipeLoc = value.indexOf('|');
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " may not have only one pipe separated argument: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf('|'))
		{
			Logging.errorPrint(getTokenName() + " may have only one pipe: "
				+ value);
			return false;
		}

		String rest = value.substring(pipeLoc + 1);
		if (hasIllegalSeparator(',', rest))
		{
			return false;
		}
		int count;
		try
		{
			count = Integer.parseInt(value.substring(0, pipeLoc));
			if (count <= 0)
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

		StringTokenizer tok = new StringTokenizer(rest, Constants.COMMA);
		List<CDOMReference<ClassSkillList>> refs =
				new ArrayList<CDOMReference<ClassSkillList>>();

		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<ClassSkillList> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SKILLLIST_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(SKILLLIST_CLASS, token);
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<ClassSkillList> rcs =
				new ReferenceChoiceSet<ClassSkillList>(refs);
		ChoiceSet<ClassSkillList> cs =
				new ChoiceSet<ClassSkillList>(getTokenName(), rcs);
		PCGraphGrantsEdge edge =
				context.getGraphContext().grant(getTokenName(), pcc, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		GrantFactory<ClassSkillList> gf =
				new GrantFactory<ClassSkillList>(edge);
		context.getGraphContext().grant(getTokenName(), pcc, gf);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		GraphChanges<ChoiceSet> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, ChoiceSet.class);
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
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : mtl.getKeySet())
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (SKILLLIST_CLASS.equals(cs.getChoiceClass()))
			{
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
					// Error
					return null;
				}
				addStrings.add(f.toString() + "|" + cs.getLSTformat());
				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
