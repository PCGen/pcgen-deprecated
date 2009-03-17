package pcgen.core.facade;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Type;

public interface SkillFacade extends CDOMObject
{

	public boolean isUntrained();

	public Type getType();

	public PCStatFacade getKeyStat();

}
