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

import pcgen.base.formula.Resolver;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.formula.FixedSizeResolver;
import pcgen.cdom.formula.FormulaSizeResolver;
import pcgen.cdom.mode.Size;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setTemplateSize(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		Resolver<Size> res;
		try
		{
			res = new FixedSizeResolver(Size.valueOf(value));
		}
		catch (IllegalArgumentException e)
		{
			res = new FormulaSizeResolver(FormulaFactory.getFormulaFor(value));
		}
		context.obj.put(template, ObjectKey.SIZE, res);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Resolver<Size> res = context.obj.getObject(template, ObjectKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[]{res.toLSTFormat()};
	}
}
