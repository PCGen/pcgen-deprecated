package pcgen.cdom.inst;

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;

public class CDOMSubstitutionClass extends CDOMPCClass implements
		CategorizedCDOMObject<CDOMSubstitutionClass>
{

	private Category<CDOMSubstitutionClass> cat;

	public void setCDOMCategory(Category<CDOMSubstitutionClass> ac)
	{
		cat = ac;
	}

	public Category<CDOMSubstitutionClass> getCDOMCategory()
	{
		return cat;
	}

	public void setKeyName(String key)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMSubstitutionClass)
		{
			CDOMSubstitutionClass other = (CDOMSubstitutionClass) o;
			boolean catEqual;
			if (cat == null)
			{
				catEqual = other.cat == null;
			}
			else
			{
				catEqual = cat.equals(other.cat);
			}
			return catEqual && other.isCDOMEqual(this)
					&& other.equalsPrereqObject(this);
		}
		return false;
	}

}
