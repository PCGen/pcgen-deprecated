package plugin.lsttokens.pcclass;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLASS Token
 */
public class ExclassToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "EXCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setExClass(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		CDOMReference<PCClass> cl =
				context.ref.getCDOMReference(PCClass.class, value);
		pcc.put(ObjectKey.EX_CLASS, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		CDOMReference<PCClass> cl = pcc.get(ObjectKey.EX_CLASS);
		if (cl == null)
		{
			return null;
		}
		return new String[]{cl.getLSTformat()};
	}
}
