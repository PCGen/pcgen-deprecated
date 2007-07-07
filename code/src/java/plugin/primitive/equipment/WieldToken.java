package plugin.primitive.equipment;

import pcgen.cdom.enumeration.EqWield;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class WieldToken implements PrimitiveToken<Equipment>
{

	private EqWield wield;

	public void initialize(LoadContext context, String value)
	{
		wield = EqWield.valueOf(value);
	}

	public String getTokenName()
	{
		return "WIELD";
	}

	public Class<Equipment> getReferenceClass()
	{
		return Equipment.class;
	}

	public String getLSTformat()
	{
		return wield.toString();
	}

	public boolean allow(PlayerCharacter pc, Equipment obj)
	{
		return wield.equals(obj.get(ObjectKey.WIELD));
	}

}
