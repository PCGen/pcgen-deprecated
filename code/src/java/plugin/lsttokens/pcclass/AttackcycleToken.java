package plugin.lsttokens.pcclass;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.Logging;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.AttackType;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken implements PCClassLstToken, PCClassClassLstToken
{

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
		throws PersistenceLayerException
	{
		if (value.indexOf('|') == -1)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ " encountered.  Requires a | : " + value);
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
		Set<String> set = new TreeSet<String>();
		for (Entry<AttackType, String> me : map.entrySet())
		{
			if (me.getKey().equals(AttackType.GRAPPLE))
			{
				// TODO Validate same as MELEE?
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
