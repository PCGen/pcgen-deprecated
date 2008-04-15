package pcgen.cdom.actor;

import pcgen.cdom.base.ChooseActor;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PlayerCharacter;

public class GrantAssociationActor<T extends PrereqObject> extends
		SimpleAssociatedObject implements LSTWriteable, ChooseActor
{

	private final CDOMSingleRef<?> masterReference;

	public GrantAssociationActor(CDOMSingleRef<?> ref)
	{
		masterReference = ref;
	}

	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO From the Association of the Choice Object (needs to be passed in
		 * to this method??), this should Grant the association (such as a
		 * WeaponProf) to the masterReference object (Such as an Ability like
		 * Weapon Focus)
		 */
		/*
		 * TODO Make sure that the assocations in assoc are placed into the
		 * edge...
		 */
	}

	public String getLSTformat()
	{
		return masterReference.getLSTformat();
	}

	@Override
	public int hashCode()
	{
		return masterReference.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof GrantAssociationActor
			&& masterReference
				.equals(((GrantAssociationActor<?>) o).masterReference);
	}
}
