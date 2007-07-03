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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	private static final Class<Deity> DEITY_CLASS = Deity.class;

	@Override
	public String getTokenName()
	{
		return "DEITY";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.clearDeityList();

		StringTokenizer st = new StringTokenizer(Constants.PIPE);
		while (st.hasMoreTokens())
		{
			pcclass.addDeity(st.nextToken());
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Deity> deity =
					context.ref.getCDOMReference(DEITY_CLASS, tokText);
			context.graph.grant(getTokenName(), pcc, deity);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		GraphChanges<Deity> changes =
				context.graph.getChangesFromToken(getTokenName(), pcc,
					DEITY_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.PIPE)};
	}
}
