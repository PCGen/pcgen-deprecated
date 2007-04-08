package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLSTAT Token
 */
public class SpellstatToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "SPELLSTAT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setSpellBaseStat(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		PCStat pcs = context.ref.getConstructedCDOMObject(PCStat.class, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in Token + "
				+ getTokenName() + ": " + value);
			return false;
		}
		pcc.put(ObjectKey.SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		PCStat pcs = pcc.get(ObjectKey.SPELL_STAT);
		if (pcs == null)
		{
			return null;
		}
		return new String[]{pcs.getKeyName()};
	}
}
