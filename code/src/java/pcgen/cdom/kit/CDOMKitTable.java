package pcgen.cdom.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;

public class CDOMKitTable extends AbstractCDOMKitObject
{
	private String tableName;
	private List<RangeLimited> list = new ArrayList<RangeLimited>();

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public void addGear(CDOMKitGear optionInfo, Formula min, Formula max)
	{
		list.add(new RangeLimited(optionInfo, min, max));
	}

	public static class RangeLimited
	{
		public final CDOMKitGear gear;
		public final Formula lowRange;
		public final Formula highRange;

		public RangeLimited(CDOMKitGear optionInfo, Formula min, Formula max)
		{
			gear = optionInfo;
			lowRange = min;
			highRange = max;
		}
	}

	public List<RangeLimited> getList()
	{
		return Collections.unmodifiableList(list);
	}
}
