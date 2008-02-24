package plugin.primitive.equipment;

import pcgen.cdom.enumeration.EqWield;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class WieldToken implements PrimitiveToken<CDOMEquipment>
{

	private EqWield wield;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		wield = EqWield.valueOf(value);
		return true;
	}

	public String getTokenName()
	{
		return "WIELD";
	}

	public Class<CDOMEquipment> getReferenceClass()
	{
		return CDOMEquipment.class;
	}

	public String getLSTformat()
	{
		return wield.toString();
	}

	public boolean allow(CharacterDataStore pc, CDOMEquipment obj)
	{
		return wield.equals(obj.get(ObjectKey.WIELD));
	}

}
