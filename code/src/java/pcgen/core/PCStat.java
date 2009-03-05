package pcgen.core;

import pcgen.cdom.base.CDOMObject;

public interface PCStat extends CDOMObject
{

	public String getName();

	public String getAbbreviation();

	@Override
	public String toString();

}
