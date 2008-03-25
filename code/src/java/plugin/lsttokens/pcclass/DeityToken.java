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
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken extends AbstractToken implements PCClassLstToken,
CDOMPrimaryToken<CDOMPCClass>
{

	private static final Class<CDOMDeity> DEITY_CLASS = CDOMDeity.class;

	@Override
	public String getTokenName()
	{
		return "DEITY";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCClassLstToken#parse(pcgen.core.PCClass,
	 *      java.lang.String, int)
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.clearDeityList();

		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String name = st.nextToken();
			if (name.trim().length() > 0)
			{
				pcclass.addDeity(name);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMPCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<CDOMDeity> deity =
					context.ref.getCDOMReference(DEITY_CLASS, tokText);
			context.getGraphContext().grant(getTokenName(), pcc, deity);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMPCClass pcc)
	{
		AssociatedChanges<CDOMDeity> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, DEITY_CLASS);
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(added,
			Constants.PIPE)};
	}

	public Class<CDOMPCClass> getTokenClass()
	{
		return CDOMPCClass.class;
	}
}
