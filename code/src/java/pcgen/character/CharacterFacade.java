package pcgen.character;

import java.util.Map;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

public interface CharacterFacade
{

	public <T extends CDOMObject> Map<T, AssociatedPrereqObject> getActive(
			PlayerCharacter pc, Class<T> cl);

	public <T extends CDOMObject> int getWeight(PlayerCharacter pc, T obj);

	public <T extends CategorizedCDOMObject<T>> Category<T> getCategory(
			PlayerCharacter pc, T obj);

	public <T extends CDOMObject> Map<T, AssociatedPrereqObject> getListContents(
			PlayerCharacter pc, CDOMList<T> list);

	public <T extends CDOMObject, R> R getListAttribute(PlayerCharacter pc,
			CDOMList<T> list, T obj, AssociationKey<R> ak);

	public int getLevel(PlayerCharacter pc);

	public int getPCClassLevel(PlayerCharacter pc, PCClass cl);

	// public Collection<ChoiceSet<?>> getUnfinishedChoices(PlayerCharacter pc);

}
