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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.core.PCTemplateList;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.ListGraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class TemplateLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	private static final Class<PCTemplateList> PCTEMPLATELIST_CLASS =
			PCTemplateList.class;

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
			// TODO TCT is not enough to keep this unique across different
			// Templates
			CDOMReference<PCTemplateList> ref =
					context.ref.getCDOMReference(PCTEMPLATELIST_CLASS, "*TCT");
			boolean returnval =
					parseChoose(context, cdo, ref, value
						.substring(Constants.LST_CHOOSE.length()));
			if (returnval)
			{
				context.graph.addSlot(getTokenName(), cdo, PCTEMPLATE_CLASS,
					FormulaFactory.getFormulaFor(1));
				// TODO Need to get the restriction attached to this slot...
			}
			return returnval;
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			CDOMReference<PCTemplateList> ref =
					context.ref.getCDOMAllReference(PCTEMPLATELIST_CLASS);
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
				context.graph.grant(getTokenName(), cdo, ref);
			}
		}

		return true;
	}

	public boolean parseChoose(LoadContext context, CDOMObject obj,
		CDOMReference<PCTemplateList> swl, String value)
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

		while (tok.hasMoreTokens())
		{
			CDOMSimpleSingleRef<PCTemplate> ref =
					context.ref.getCDOMReference(PCTEMPLATE_CLASS, tok
						.nextToken());
			context.list.addToList(getTokenName(), obj, swl, ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		GraphChanges<PCTemplate> changes =
				context.graph.getChangesFromToken(getTokenName(), cdo,
					PCTEMPLATE_CLASS);

		CDOMReference<PCTemplateList> tctRef =
				context.ref.getCDOMReference(PCTEMPLATELIST_CLASS, "*TCT");
		CDOMReference<PCTemplateList> allRef =
				context.ref.getCDOMAllReference(PCTEMPLATELIST_CLASS);

		ListGraphChanges<PCTemplate> tctChanges =
				context.list.getChangesInList(getTokenName(), cdo, tctRef);
		ListGraphChanges<PCTemplate> allChanges =
				context.list.getChangesInList(getTokenName(), cdo, allRef);

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
