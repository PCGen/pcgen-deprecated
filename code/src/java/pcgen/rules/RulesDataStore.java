package pcgen.rules;

import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.inst.CDOMPCLevel;

public interface RulesDataStore
{
	public CDOMPCLevel getLevel(int level);

	public <T extends CDOMObject> Set<T> getAll(Class<T> cl);

	public <T extends CDOMObject> T getObject(Class<T> cl, String key);

	public CDOMSizeAdjustment getNextSize(CDOMSizeAdjustment size);

	public CDOMSizeAdjustment getPreviousSize(CDOMSizeAdjustment size);

	public CDOMSizeAdjustment getDefaultSizeAdjustment();

	public <TT extends CDOMObject> CDOMReference<TT> getReference(Class<TT> cl,
			String name);
}
