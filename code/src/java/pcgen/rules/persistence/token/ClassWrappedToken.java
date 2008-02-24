/**
 * 
 */
package pcgen.rules.persistence.token;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

public class ClassWrappedToken implements
		CDOMCompatibilityToken<CDOMPCClassLevel>
{

	private static int wrapIndex = Integer.MIN_VALUE;

	private static final Integer ONE = Integer.valueOf(1);

	private CDOMToken<CDOMPCClass> wrappedToken;

	private int priority = wrapIndex++;

	public Class<CDOMPCClassLevel> getTokenClass()
	{
		return CDOMPCClassLevel.class;
	}

	public ClassWrappedToken(CDOMToken<CDOMPCClass> tok)
	{
		wrappedToken = tok;
	}

	public boolean parse(LoadContext context, CDOMPCClassLevel obj, String value)
			throws PersistenceLayerException
	{
		if (ONE.equals(obj.get(IntegerKey.LEVEL)))
		{
			CDOMPCClass parent = (CDOMPCClass) obj.get(ObjectKey.PARENT);
			return wrappedToken.parse(context, parent, value);
		}
		return false;
	}

	public String getTokenName()
	{
		return wrappedToken.getTokenName();
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return priority;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

}