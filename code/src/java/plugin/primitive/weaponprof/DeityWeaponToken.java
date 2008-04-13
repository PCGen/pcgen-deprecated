package plugin.primitive.weaponprof;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class DeityWeaponToken implements PrimitiveToken<CDOMWeaponProf>
{

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		if (value != null)
		{
			return false;
			// throw new IllegalArgumentException(
			// "Deity Weapon Primitive does not allow a value");
		}
		return true;
	}

	public String getTokenName()
	{
		return "DEITY";
	}

	public Class<CDOMWeaponProf> getReferenceClass()
	{
		return CDOMWeaponProf.class;
	}

	public Set<CDOMWeaponProf> getSet(CharacterDataStore pc)
	{
		List<CDOMDeity> deities = pc.getActiveGraph().getGrantedNodeList(
				CDOMDeity.class);
		Set<CDOMWeaponProf> wpSet = new HashSet<CDOMWeaponProf>();
		if (deities == null)
		{
			return wpSet;
		}
		for (CDOMDeity deity : deities)
		{
			List<CDOMReference<CDOMWeaponProf>> weapons = deity
					.getListFor(ListKey.DEITYWEAPON);
			if (weapons != null)
			{
				for (CDOMReference<CDOMWeaponProf> wpRef : weapons)
				{
					wpSet.addAll(wpRef.getContainedObjects());
				}
			}
		}
		return wpSet;
	}

	public String getLSTformat()
	{
		return null;
	}

	public boolean allow(CharacterDataStore pc, CDOMWeaponProf obj)
	{
		List<CDOMDeity> deities = pc.getActiveGraph().getGrantedNodeList(
				CDOMDeity.class);
		if (deities == null)
		{
			return false;
		}
		for (CDOMDeity deity : deities)
		{
			List<CDOMReference<CDOMWeaponProf>> weapons = deity
					.getListFor(ListKey.DEITYWEAPON);
			if (weapons != null)
			{
				for (CDOMReference<CDOMWeaponProf> wpRef : weapons)
				{
					if (wpRef.contains(obj))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

}
