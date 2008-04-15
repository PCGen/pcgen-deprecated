/**
 * 
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.PCClass;

public class CategorizedReferenceManufacturer<T extends CDOMObject & CategorizedCDOMObject<T>>
		extends AbstractReferenceManufacturer<T> implements
		ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>>
{

	private final Category<T> category;

	public CategorizedReferenceManufacturer(Class<T> cl, Category<T> cat)
	{
		super(cl);
		category = cat;
	}

	public CDOMCategorizedSingleRef<T> getReference(String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
		if (val.equals(""))
		{
			throw new IllegalArgumentException(val);
		}
		try
		{
			Integer.parseInt(val);
			throw new IllegalArgumentException(val);
		}
		catch (NumberFormatException nfe)
		{
			// ok
		}
		if (val.startsWith("TYPE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ANY"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ALL"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("PRE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CHOOSE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMES="))
		{
			throw new IllegalArgumentException(val);
		}
		if (getCDOMClass().equals(PCClass.class))
		{
			if (val.startsWith("CLASS"))
			{
				throw new IllegalArgumentException(val);
			}
			else if (val.startsWith("SUB"))
			{
				throw new IllegalArgumentException(val);
			}
			else
			{
				try
				{
					Integer.parseInt(val);
					throw new IllegalArgumentException(val);
				}
				catch (NumberFormatException nfe)
				{
					// Want this!
				}
			}
		}

		return new CDOMCategorizedSingleRef<T>(getCDOMClass(), category, val);
	}

	//
	// public CDOMTypeRef<T> getTypeReference(String subStr)
	// {
	// if (subStr.length() == 0)
	// {
	// Logging.errorPrint("Type may not be empty in: " + subStr);
	// return null;
	// }
	// if (subStr.charAt(0) == '.'
	// || subStr.charAt(subStr.length() - 1) == '.')
	// {
	// Logging
	// .errorPrint("Type may not start or end with . in: " + subStr);
	// return null;
	// }
	// String[] types = subStr.split("\\.");
	// return getTypeReference(types);
	// }

}