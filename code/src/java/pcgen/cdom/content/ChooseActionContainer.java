package pcgen.cdom.content;

import java.util.Collection;
import java.util.Set;

import pcgen.base.util.ListSet;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.helper.ChooseActor;

public class ChooseActionContainer extends ConcretePrereqObject implements
		LSTWriteable
{

	private String name;

	private Set<ChooseActor> actorSet = new ListSet<ChooseActor>();

	public ChooseActionContainer(String nm)
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
		return 23 * actorSet.size();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof ChooseActionContainer
			&& actorSet.equals(((ChooseActionContainer) o).actorSet);
	}

	public String getLSTformat()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getName()
	{
		return name;
	}
}
