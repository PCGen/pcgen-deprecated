package pcgen.cdom.factory;

import java.util.Collection;

import pcgen.cdom.base.AssociationSupport;
import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PlayerCharacter;

public abstract class AbstractFactory<T extends PrereqObject> extends
		ConcretePrereqObject implements LSTWriteable
{

	private PCGraphEdge e = null;

	private final CDOMEdgeReference edgeRef;

	private AssociationSupport assoc;

	public AbstractFactory(PCGraphGrantsEdge edge)
	{
		if (edge == null)
		{
			throw new IllegalArgumentException();
		}
		edgeRef = null;
		e = edge;
	}

	public AbstractFactory(CDOMEdgeReference ref)
	{
		if (ref == null)
		{
			throw new IllegalArgumentException();
		}
		edgeRef = ref;
	}

	public abstract void execute(PlayerCharacter pc);

	protected PCGraphEdge getEdge()
	{
		if (e == null)
		{
			e = edgeRef.resolvesTo();
		}
		return e;
	}

	public <AT> AT getAssociation(AssociationKey<AT> ak)
	{
		return assoc == null ? null : assoc.getAssociation(ak);
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

	@Override
	public int hashCode()
	{
		return e == null ? 0 : e.hashCode();
	}

	public boolean equalsAbstractFactory(AbstractFactory<?> other)
	{
		if (other == this)
		{
			return true;
		}
		if (assoc == null || !assoc.hasAssociations())
		{
			return edgeMatches(other)
				&& (other.assoc == null || !other.assoc.hasAssociations());
		}
		return edgeMatches(other) && assoc.equals(other.assoc);
	}

	private boolean edgeMatches(AbstractFactory<?> other)
	{
		return (e == null && other.e == null)
			|| (e != null && e.equals(other.e));
	}

	public String getLSTformat()
	{
		throw new UnsupportedOperationException();
	}

	public boolean usesChoiceSet(ChoiceSet<?> cs)
	{
		return cs != null && cs.equals(getEdge().getNodeAt(1));
	}
}
