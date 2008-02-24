package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCStatLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with ABB Token for pc stat
 */
public class AbbToken implements PCStatLstToken, CDOMPrimaryToken<CDOMStat>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse ABB token
	 * 
	 * @param stat
	 * @param value
	 * @return true
	 */
	public boolean parse(PCStat stat, String value)
	{
		stat.setAbb(value);
		return true;
	}

	public Class<CDOMStat> getTokenClass()
	{
		return CDOMStat.class;
	}

	public boolean parse(LoadContext context, CDOMStat obj, String value)
			throws PersistenceLayerException
	{
		//context.ref.reassociateKey(value, obj);
		context.ref.registerAbbreviation(obj, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMStat obj)
	{
		String abb = context.ref.getAbbreviation(obj);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

}
