package pcgen.core.facade;

import java.util.List;
import pcgen.cdom.enumeration.Type;

public interface PCClassFacade
{

	public List<Type> getTypes();

	public String getHitDie();

	public Type getSpellType();

	public PCStatFacade getSpellStat();

}
