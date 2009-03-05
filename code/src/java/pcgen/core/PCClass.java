package pcgen.core;

import java.util.List;
import pcgen.cdom.enumeration.Type;

public interface PCClass
{

	public List<Type> getTypes();

	public String getHitDie();

	public Type getSpellType();

	public PCStat getSpellStat();

}
