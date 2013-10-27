package plugin.lsttokens.statsandchecks.alignment;

import pcgen.cdom.inst.CDOMAlignment;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCAlignmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with ABB Token for pc alignment
 */
public class AbbToken implements PCAlignmentLstToken,
		CDOMPrimaryToken<CDOMAlignment>
{

	/**
	 * Return the token name
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
	 * @param align
	 * @param value
	 * @return true
	 */
	public boolean parse(PCAlignment align, String value)
	{
		align.setKeyName(value);
		return true;
	}

	public Class<CDOMAlignment> getTokenClass()
	{
		return CDOMAlignment.class;
	}

	public boolean parse(LoadContext context, CDOMAlignment obj, String value)
			throws PersistenceLayerException
	{
		//context.ref.reassociateReference(value, obj);
		context.ref.registerAbbreviation(obj, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAlignment obj)
	{
		String abb = context.ref.getAbbreviation(obj);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}
}
