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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.ListChoiceSet;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.core.PCTemplateChooseList;
import pcgen.core.PObject;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 * 
 */
public class TemplateLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	private static final Class<PCTemplateChooseList> PCTEMPLATECHOOSELIST_CLASS =
			PCTemplateChooseList.class;

	@Override
	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!(obj instanceof Campaign))
		{
			obj.addTemplate(value);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
	{
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			PCTemplateChooseList tcl = cdo.getCDOMTemplateChooseList();
			CDOMSingleRef<PCTemplateChooseList> ref =
					context.ref.getCDOMDirectReference(tcl);
			boolean returnval =
					parseChoose(context, cdo, ref, value
						.substring(Constants.LST_CHOOSE.length()));
			if (returnval)
			{
				ChooseActionContainer container =
						new ChooseActionContainer(getTokenName());
				container.addActor(new GrantActor<PCTemplate>());
				AssociatedPrereqObject edge =
						context.getGraphContext().grant(getTokenName(), cdo,
							container);
				edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
					.getFormulaFor(1));
				edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT,
					FormulaFactory.getFormulaFor(1));
				ListChoiceSet<PCTemplate> rcs =
						new ListChoiceSet<PCTemplate>(tcl);
				ChoiceSet<PCTemplate> cs =
						new ChoiceSet<PCTemplate>("ADD", rcs);
				edge.setAssociation(AssociationKey.CHOICE, cs);
			}
			return returnval;
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			PCTemplateChooseList tcl = cdo.getCDOMTemplateChooseList();
			CDOMSingleRef<PCTemplateChooseList> ref =
					context.ref.getCDOMDirectReference(tcl);
			return parseChoose(context, cdo, ref, value
				.substring(Constants.LST_ADDCHOICE.length()));
		}
		else
		{
			if (isEmpty(value) || hasIllegalSeparator('|', value))
			{
				return false;
			}

			StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				CDOMSimpleSingleRef<PCTemplate> ref =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText);
				context.getGraphContext().grant(getTokenName(), cdo, ref);
			}
		}

		return true;
	}

	public boolean parseChoose(LoadContext context, CDOMObject obj,
		CDOMReference<PCTemplateChooseList> swl, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			CDOMSimpleSingleRef<PCTemplate> ref =
					context.ref.getCDOMReference(PCTEMPLATE_CLASS, tok
						.nextToken());
			context.getListContext().addToList(getTokenName(), obj, swl, ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		AssociatedChanges<PCTemplate> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					cdo, PCTEMPLATE_CLASS);

		PCTemplateChooseList tcl = cdo.getCDOMTemplateChooseList();
		CDOMSingleRef<PCTemplateChooseList> ref =
				context.ref.getCDOMDirectReference(tcl);
		CDOMReference<PCTemplateChooseList> allRef =
				context.ref.getCDOMAllReference(PCTEMPLATECHOOSELIST_CLASS);

		AssociatedChanges<CDOMReference<PCTemplate>> tctChanges =
				context.getListContext().getChangesInList(getTokenName(), cdo,
					ref);
		AssociatedChanges<CDOMReference<PCTemplate>> allChanges =
				context.getListContext().getChangesInList(getTokenName(), cdo,
					allRef);

		if (changes == null && tctChanges == null && allChanges == null)
		{
			// No templates
			return null;
		}

		List<String> list = new ArrayList<String>();

		if (changes != null)
		{
			if (changes.hasRemovedItems() || changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
					+ "does not support .CLEAR");
				return null;
			}
			Collection<LSTWriteable> added = changes.getAdded();
			if (added != null && !added.isEmpty())
			{
				list.add(ReferenceUtilities.joinLstFormat(changes.getAdded(),
					Constants.PIPE));
			}
		}

		if (tctChanges != null)
		{
			if (tctChanges.hasRemovedItems()
				|| tctChanges.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
					+ "does not support .CLEAR");
				return null;
			}
			if (tctChanges.hasAddedItems())
			{
				list.add(Constants.LST_CHOOSE
					+ ReferenceUtilities.joinLstFormat(tctChanges.getAdded(),
						Constants.PIPE));
			}
		}
		if (allChanges != null)
		{
			if (allChanges.hasRemovedItems()
				|| allChanges.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
					+ "does not support .CLEAR");
				return null;
			}
			if (allChanges.hasAddedItems())
			{
				list.add(Constants.LST_ADDCHOICE
					+ ReferenceUtilities.joinLstFormat(allChanges.getAdded(),
						Constants.PIPE));
			}
		}
		if (list.isEmpty())
		{
			// Possible if none triggered
			return null;
		}
		return list.toArray(new String[list.size()]);
	}
}
