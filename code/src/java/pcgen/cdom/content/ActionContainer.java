package pcgen.cdom.content;

import java.util.Collection;

import pcgen.cdom.helper.ChooseActor;

public interface ActionContainer
{
	public void addActor(ChooseActor a);
	public Collection<ChooseActor> getActors();

}
