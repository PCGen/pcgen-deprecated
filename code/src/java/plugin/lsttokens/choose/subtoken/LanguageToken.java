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
package plugin.lsttokens.choose.subtoken;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetToken;

public class LanguageToken extends AbstractToken implements
		ChoiceSetToken<CDOMObject>
{

	private static final Class<CDOMLanguage> LANGUAGE_CLASS = CDOMLanguage.class;

	@Override
	public String getTokenName()
	{
		return "LANGUAGE";
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject obj,
			String value) throws PersistenceLayerException
	{
		return context.getChoiceSet(LANGUAGE_CLASS, value);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
