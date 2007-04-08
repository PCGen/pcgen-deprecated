package plugin.lsttokens.pcclass;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.SkillList;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with ISMONSTER Token
 */
public class IsmonsterToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "ISMONSTER";
	}

	public boolean parse(final PCClass pcclass, final String value,
		final int level)
	{
		if (value.startsWith("Y")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(true);
		}
		else if (value.startsWith("N")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(false);
		}
		else
		{
			Logging.errorPrint("Unknown option " + value + " in "
				+ getTokenName());
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
					+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
			CDOMGroupRef<SkillList> msl =
					context.ref
						.getCDOMTypeReference(SkillList.class, "Monster");
			context.graph.linkObjectIntoGraph(getTokenName(), pcc, msl);
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		pcc.put(ObjectKey.IS_MONSTER, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean isM = pcc.get(ObjectKey.IS_MONSTER);
		if (isM == null)
		{
			return null;
		}
		return new String[]{isM.booleanValue() ? "YES" : "NO"};
	}
}
