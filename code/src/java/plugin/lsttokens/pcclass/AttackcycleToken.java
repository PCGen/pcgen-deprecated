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

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.AttackType;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	@Override
	public String getTokenName()
	{
		return "ATTACKCYCLE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (value.indexOf('|') == -1)
		{
			return true;
		}

		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens())
		{
			AttackType at = AttackType.getInstance(aTok.nextToken());
			if (AttackType.GRAPPLE.equals(at))
			{
				Logging.errorPrint("Error: Cannot Set Attack Cycle "
					+ "for GRAPPLE Attack Type");
				return false;
			}
			String cycle = aTok.nextToken();
			pcclass.setAttackCycle(at, cycle);
			/*
			 * This is a bit of a hack - it is designed to account for the fact
			 * that the BAB tag in ATTACKCYCLE actually impacts both
			 * ATTACK.MELEE and ATTACK.GRAPPLE ... therefore, one method of
			 * handing this (which is done here) is to actually allow the
			 * pcgen.core code to keep the 4 attack type view (MELEE, RANGED,
			 * UNARMED, GRAPPLE) by simply loading the attackCycle for MELEE
			 * into GRAPPLE. This is done in the hope that this is a more
			 * flexible solution for potential future requirements for other
			 * attack types (rather than treating GRAPPLE as a special case
			 * throughout the core code) - thpr 11/1/06
			 */
			if (at.equals(AttackType.MELEE))
			{
				pcclass.setAttackCycle(AttackType.GRAPPLE, cycle);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		if (aTok.countTokens() % 2 == 1)
		{
			Logging.errorPrint(getTokenName()
				+ " must have an even number of argumetns.");
			return false;
		}

		while (aTok.hasMoreTokens())
		{
			AttackType at = AttackType.getInstance(aTok.nextToken());
			if (AttackType.GRAPPLE.equals(at))
			{
				Logging.errorPrint("Error: Cannot Set Attack Cycle "
					+ "for GRAPPLE Attack Type");
				return false;
			}
			String cycle = aTok.nextToken();
			pcc.setAttackCycle(at, cycle);
			/*
			 * This is a bit of a hack - it is designed to account for the fact
			 * that the BAB tag in ATTACKCYCLE actually impacts both
			 * ATTACK.MELEE and ATTACK.GRAPPLE ... therefore, one method of
			 * handing this (which is done here) is to actually allow the
			 * pcgen.core code to keep the 4 attack type view (MELEE, RANGED,
			 * UNARMED, GRAPPLE) by simply loading the attackCycle for MELEE
			 * into GRAPPLE. This is done in the hope that this is a more
			 * flexible solution for potential future requirements for other
			 * attack types (rather than treating GRAPPLE as a special case
			 * throughout the core code) - thpr Nov 1, 2006
			 */
			if (at.equals(AttackType.MELEE))
			{
				pcc.setAttackCycle(AttackType.GRAPPLE, cycle);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Map<AttackType, String> map = pcc.getAttackCycle();
		if (map == null || map.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Entry<AttackType, String> me : map.entrySet())
		{
			if (me.getKey().equals(AttackType.GRAPPLE))
			{
				// Validate same as MELEE
				if (!me.getValue().equals(map.get(AttackType.MELEE)))
				{
					context
						.addWriteMessage("Grapple Attack Cycle MUST be equal to "
							+ "Melee Attack Cycle because it is not stored");
					return null;
				}
			}
			else
			{
				set.add(me.getKey().getIdentifier() + Constants.PIPE
					+ me.getValue());
			}
		}
		return new String[]{StringUtil.join(set, Constants.PIPE)};
	}
}
