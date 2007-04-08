package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLTYPE Token
 */
public class SpelltypeToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		/*
		 * CONSIDER In the future it may be useful here to check for "" or
		 * "None" and filter those out (never set the spell type) - thpr 11/9/06
		 */
		pcclass.setSpellType(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		Type typeCon = Type.getConstant(value);
		pcc.put(ObjectKey.SPELL_TYPE, typeCon);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Type type = pcc.get(ObjectKey.SPELL_TYPE);
		if (type == null)
		{
			return null;
		}
		return new String[]{type.toString()};
	}
}
