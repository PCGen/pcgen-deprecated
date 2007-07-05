package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.PObject;

public class Qualifier
{

	private final Class<? extends PObject> qualClass;
	private final CDOMReference<? extends PObject> qualRef;

	public Qualifier(Class<? extends PObject> cl,
		CDOMReference<? extends PObject> ref)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		if (ref == null)
		{
			throw new IllegalArgumentException("Referece cannot be null");
		}
		qualClass = cl;
		qualRef = ref;
	}

	public Class<? extends PObject> getQualifiedClass()
	{
		return qualClass;
	}

	public CDOMReference<? extends PObject> getQualifiedReference()
	{
		return qualRef;
	}

}
