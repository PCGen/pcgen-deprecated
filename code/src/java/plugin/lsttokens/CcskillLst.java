/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.core.SkillList;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class CcskillLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	private static final Class<SkillList> SKILLLIST_CLASS = SkillList.class;

	@Override
	public String getTokenName()
	{
		return "CCSKILL";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			obj.addCcSkill(tok.nextToken());
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		/*
		 * TODO Question is whether the use of this in a PCClass should really
		 * alter the SkillList attached to the PCClass...
		 */
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean first = true;

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
						+ ": .CLEAR was not the first list item");
					return false;
				}
				context.graph.removeAll(getTokenName(), obj, SKILL_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Skill> ref;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					ref = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								SKILL_CLASS, clearText);
				}
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				context.graph.remove(getTokenName(), obj, ref);
			}
			else
			{
				/*
				 * Note this HAS to be done one-by-one, because the
				 * .clearChildNodeOfClass method above does NOT recognize the
				 * C/CC Skill object and therefore doesn't know how to search
				 * the sublists
				 */
				CDOMReference<Skill> ref;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					foundOther = true;
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								SKILL_CLASS, tokText);
				}
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				AssociatedPrereqObject edge =
						context.list.addToMasterList(getTokenName(), obj,
							context.ref.getCDOMReference(SKILLLIST_CLASS,
								"*Allowed"), ref);
				edge.setAssociation(AssociationKey.SKILL_COST,
					SkillCost.CROSS_CLASS);
			}
			first = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		CDOMGroupRef<SkillList> listRef =
				context.ref.getCDOMAllReference(SKILLLIST_CLASS);
		GraphChanges<Skill> changes =
				context.list.getChangesInMasterList(getTokenName(), obj,
					listRef);
		if (changes == null)
		{
			// Legal if no CSKILL was present
			return null;
		}
		List<String> list = new ArrayList<String>();
		if (changes.hasRemovedItems())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
					+ getTokenName()
					+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
				+ ReferenceUtilities.joinLstFormat(changes.getRemoved(),
					",|.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		if (changes.hasAddedItems())
		{
			Collection<LSTWriteable> addedList = changes.getAdded();
			for (LSTWriteable added : addedList)
			{
				AssociatedPrereqObject assoc =
						changes.getAddedAssociation(added);
				if (!SkillCost.CROSS_CLASS.equals(assoc
					.getAssociation(AssociationKey.SKILL_COST)))
				{
					context
						.addWriteMessage("Skill Cost must be CROSS_CLASS for Token "
							+ getTokenName());
					return null;
				}
			}
			list.add(ReferenceUtilities
				.joinLstFormat(addedList, Constants.PIPE));
		}
		return list.toArray(new String[list.size()]);
	}
}
