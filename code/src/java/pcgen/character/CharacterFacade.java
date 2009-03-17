package pcgen.character;

import java.util.Map;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.facade.PCClassFacade;
import pcgen.core.facade.PlayerCharacterFacade;

public interface CharacterFacade
{

	public <T extends CDOMObject> Map<T, AssociatedPrereqObject> getActive(
			PlayerCharacterFacade pc, Class<T> cl);

	public <T extends CDOMObject> int getWeight(PlayerCharacterFacade pc, T obj);

	public <T extends CategorizedCDOMObject<T>> Category<T> getCategory(
			PlayerCharacterFacade pc, T obj);

	public <T extends CDOMObject> Map<T, AssociatedPrereqObject> getListContents(
			PlayerCharacterFacade pc, CDOMList<T> list);

	public <T extends CDOMObject, R> R getListAttribute(PlayerCharacterFacade pc,
			CDOMList<T> list, T obj, AssociationKey<R> ak);

	public int getLevel(PlayerCharacterFacade pc);

	public int getPCClassLevel(PlayerCharacterFacade pc, PCClassFacade cl);

	// public Collection<ChoiceSet<?>> getUnfinishedChoices(PlayerCharacter pc);

}
