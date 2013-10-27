package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.SizeAdjustmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken implements SizeAdjustmentLstToken,
		CDOMPrimaryToken<CDOMSizeAdjustment>
{

	/**
	 * Get the token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse the abbreviation token
	 * 
	 * @param sa
	 * @param value
	 *            true
	 * @return true
	 */
	public boolean parse(SizeAdjustment sa, String value)
	{
		sa.setAbbreviation(value);
		return true;
	}

	public Class<CDOMSizeAdjustment> getTokenClass()
	{
		return CDOMSizeAdjustment.class;
	}

	public boolean parse(LoadContext context, CDOMSizeAdjustment obj,
			String value) throws PersistenceLayerException
	{
		//context.ref.reassociateReference(value, obj);
		context.ref.registerAbbreviation(obj, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSizeAdjustment obj)
	{
		String abb = context.ref.getAbbreviation(obj);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

}
