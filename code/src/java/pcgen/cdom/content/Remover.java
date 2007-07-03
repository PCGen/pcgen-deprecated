package pcgen.cdom.content;

import java.util.Collection;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociationSupport;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.enumeration.AssociationKey;

public class Remover<T extends PrereqObject> extends ConcretePrereqObject
		implements LSTWriteable
{
	private final Class<T> cl;
	private final Formula removeCount;
	private AssociationSupport assoc;

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

	public String getLSTformat()
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

	public <AT> AT getAssociation(AssociationKey<AT> name)
	{
		return assoc == null ? null : assoc.getAssociation(name);
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return assoc == null ? null : assoc.getAssociationKeys();
	}

	public boolean hasAssociations()
	{
		return assoc != null && assoc.hasAssociations();
	}

	public <AT> void setAssociation(AssociationKey<AT> name, AT value)
	{
		if (assoc == null)
		{
			assoc = new AssociationSupport();
		}
		assoc.setAssociation(name, value);
	}
}
