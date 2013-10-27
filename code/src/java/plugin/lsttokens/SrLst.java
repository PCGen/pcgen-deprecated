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
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.SpellResistance;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class SrLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "SR";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (".CLEAR".equals(value))
		{
			obj.clearSRList();
		}
		else
		{
			obj.setSR(anInt, value);
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (".CLEAR".equals(value))
		{
			context.getObjectContext().revokeAll(getTokenName(), obj);
		}
		else
		{
			context.getObjectContext().give(getTokenName(), obj,
					getSpellResistance(value));
		}
		return true;
	}

	private SpellResistance getSpellResistance(String value)
	{
		return new SpellResistance(FormulaFactory.getFormulaFor(value));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<SpellResistance> changes = context
				.getObjectContext().getGivenChanges(getTokenName(), obj, SpellResistance.class);
		Collection<SpellResistance> added = changes.getAdded();
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (added == null || added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		if (added != null)
		{
			Set<String> set = new TreeSet<String>();
			for (LSTWriteable lw : added)
			{
				set.add(lw.getLSTformat());
			}
			list.addAll(set);
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
