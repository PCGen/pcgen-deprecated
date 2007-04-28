package pcgen.cdom.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.enumeration.AssociationKey;

public class Remover<T extends PrereqObject> extends ConcretePrereqObject
{
	private final Class<T> cl;
	private final Formula removeCount;

	public Remover(Class<T> removedClass)
	{
		if (removedClass == null)
		{
			throw new IllegalArgumentException();
		}
		cl = removedClass;
		removeCount = FormulaFactory.getFormulaFor("1");
	}

	public Remover(Class<T> removedClass, Formula count)
	{
		if (removedClass == null)
		{
			throw new IllegalArgumentException();
		}
		cl = removedClass;
		removeCount = count;
	}

	public Class<T> getRemovedClass()
	{
		return cl;
	}

	public boolean isValid(PrereqObject o)
	{
		if (!cl.isAssignableFrom(o.getClass()))
		{
			return false;
		}
		if (!hasSinkRestrictions())
		{
			return true;
		}
		for (Restriction r : getSinkRestrictions())
		{
			if (!r.qualifies(o))
			{
				return false;
			}
		}
		return true;
	}

	public String toLSTform()
	{
		return removeCount.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Slot: ").append(removeCount.toString());
		sb.append(" objects of ").append(cl.getSimpleName());
		if (hasSinkRestrictions())
		{
			sb.append(" [").append(getSinkRestrictions().toString())
				.append(']');
		}
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return removeCount.hashCode() ^ cl.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof Remover))
		{
			return false;
		}
		Remover<?> otherRemover = (Remover) o;
		return cl.equals(otherRemover.cl)
			&& removeCount.equals(otherRemover.removeCount);
	}

	/*
	 * CONSIDER Use AssociationSupport? - Tom Parker Apr 7 07
	 */
	private Map<AssociationKey<?>, Object> associationMap;

	public <AKT> void setAssociation(AssociationKey<AKT> key, AKT value)
	{
		if (associationMap == null)
		{
			associationMap = new HashMap<AssociationKey<?>, Object>();
		}
		associationMap.put(key, value);
	}

	public <AKT> AKT getAssociation(AssociationKey<AKT> key)
	{
		return (AKT) (associationMap == null ? null : associationMap.get(key));
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return new HashSet<AssociationKey<?>>(associationMap.keySet());
	}

	public boolean hasAssociations()
	{
		return associationMap != null && !associationMap.isEmpty();
	}
}
