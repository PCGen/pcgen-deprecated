package pcgen.cdom.inst;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.AssociationSupport;

public class SimpleAssociatedObject extends ConcretePrereqObject implements
		AssociatedPrereqObject
{

	private final AssociationSupport assoc = new AssociationSupport();

	public <T> T getAssociation(AssociationKey<T> name)
	{
		return assoc.getAssociation(name);
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return assoc.getAssociationKeys();
	}

	public boolean hasAssociations()
	{
		return assoc.hasAssociations();
	}

	public <T> void setAssociation(AssociationKey<T> name, T value)
	{
		assoc.setAssociation(name, value);
	}
}
