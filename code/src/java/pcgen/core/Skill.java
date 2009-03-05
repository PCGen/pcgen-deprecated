package pcgen.core;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Type;

public interface Skill extends CDOMObject
{

	public boolean isUntrained();

	public Type getType();

	public PCStat getKeyStat();

}
