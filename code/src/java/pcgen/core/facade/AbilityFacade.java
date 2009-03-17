package pcgen.core.facade;

import java.util.List;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.enumeration.Type;

public interface AbilityFacade extends CategorizedCDOMObject<AbilityFacade>, CDOMObject
{

	public List<Type> getTypes();

	public boolean isMult();

	public boolean isStackable();

	public String getDescription();

}
