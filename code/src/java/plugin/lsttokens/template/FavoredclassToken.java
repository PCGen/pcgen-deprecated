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
package plugin.lsttokens.template;

import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSubClass;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with FAVOREDCLASS Token
 */
public class FavoredclassToken extends AbstractToken implements
		PCTemplateLstToken, CDOMPrimaryToken<CDOMTemplate>
{
	public static final Class<CDOMPCClass> PCCLASS_CLASS = CDOMPCClass.class;
	public static final Class<CDOMSubClass> SUBCLASS_CLASS = CDOMSubClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVOREDCLASS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setFavoredClass(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template, String value)
	{
		return parseFavoredClass(context, template, value);
	}

	public boolean parseFavoredClass(LoadContext context, CDOMObject cdo,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<? extends CDOMPCClass> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				foundOther = true;
				if (hasIllegalSeparator('.', token))
				{
					return false;
				}
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					//Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					//SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
			}
			context.getObjectContext().addToList(cdo, ListKey.FAVORED_CLASS,
				ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		Changes<CDOMReference<? extends CDOMPCClass>> changes =
				context.getObjectContext().getListChanges(pct,
					ListKey.FAVORED_CLASS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		SortedSet<String> set = new TreeSet<String>();
		for (CDOMReference<? extends CDOMPCClass> ref : changes.getAdded())
		{
			Class<? extends CDOMPCClass> refClass = ref.getReferenceClass();
			if (SUBCLASS_CLASS.equals(refClass))
			{
				Category<CDOMSubClass> parent = ((CategorizedCDOMReference<CDOMSubClass>) ref)
						.getCDOMCategory();
				set.add(parent.toString() + "." + ref.getLSTformat());
			}
			else 
			{
				set.add(ref.getLSTformat());
			}
		}
		return new String[] { StringUtil.join(set, Constants.PIPE) };
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
