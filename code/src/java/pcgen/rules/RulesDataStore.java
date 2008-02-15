package pcgen.rules;

import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.PCCharacterLevel;
import pcgen.core.SizeAdjustment;

public interface RulesDataStore
{
	public PCCharacterLevel getLevel(int level);

	public <T extends CDOMObject> Set<T> getAll(Class<T> cl);

	public <T extends CDOMObject> T getObject(Class<T> cl, String key);

	public SizeAdjustment getNextSize(SizeAdjustment size);

	public SizeAdjustment getPreviousSize(SizeAdjustment size);

	public SizeAdjustment getDefaultSizeAdjustment();

	public <TT extends CDOMObject> CDOMReference<TT> getReference(Class<TT> cl,
			String name);
}
