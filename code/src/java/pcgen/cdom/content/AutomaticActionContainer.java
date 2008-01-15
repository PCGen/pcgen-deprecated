package pcgen.cdom.content;

import java.util.Collection;
import java.util.Set;

import pcgen.base.util.ListSet;
import pcgen.cdom.base.AssociationSupport;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ChooseActor;

public class AutomaticActionContainer extends ConcretePrereqObject implements
		LSTWriteable, ActionContainer
{

	private final AssociationSupport assoc = new AssociationSupport();

	private final String name;

	private final Set<ChooseActor> actorSet = new ListSet<ChooseActor>();

	private ChoiceSet<?> choiceSet;

	public AutomaticActionContainer(String nm)
	{
		if (nm == null)
		{
			throw new IllegalArgumentException();
		}
		name = nm;
	}

	public void addActor(ChooseActor a)
	{
		actorSet.add(a);
	}

	public Collection<ChooseActor> getActors()
	{
		return actorSet;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + 23 * actorSet.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o == null)
		{
			return false;
		}
		if (o instanceof AutomaticActionContainer)
		{
			AutomaticActionContainer other = (AutomaticActionContainer) o;
			if (choiceSet == null)
			{
				if (other.choiceSet != null)
				{
					return false;
				}
			}
			else if (!choiceSet.equals(other.choiceSet))
			{
				return false;
			}
			return name.equals(other.name) && actorSet.equals(other.actorSet)
				&& assoc.equals(other.assoc);
		}
		return false;
	}

	public String getLSTformat()
	{
		//TODO need to ensure this is consistent with equals :P
		return name + (choiceSet == null ? "" : choiceSet.getLSTformat());
	}

	public String getName()
	{
		return name;
	}

	public void setChoiceSet(ChoiceSet<?> chooser)
	{
		choiceSet = chooser;
	}

	public ChoiceSet<?> getChoiceSet()
	{
		return choiceSet;
	}

	public <T> T getAssociation(AssociationKey<T> ak)
	{
		return assoc.getAssociation(ak);
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
