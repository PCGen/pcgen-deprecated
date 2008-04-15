/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import java.util.ArrayList;

import pcgen.util.Logging;

public final class Description extends TextProperty
{

	private static final String VAR_MARKER = "$$VAR:"; //$NON-NLS-1$

	private final ArrayList<String> theComponents = new ArrayList<String>();

	private int variableCount;

	private Description()
	{
		super();
	}

	public boolean isValid()
	{
		return variableCount == getVariableCount();
	}

	private void addComponent(String s)
	{
		theComponents.add(s);
	}

	private void trimToSize()
	{
		theComponents.trimToSize();
	}

	private void setVariableCount(int maxVar)
	{
		variableCount = maxVar;
	}

	public static Description getDescriptionFromString(String str)
	{
		Description desc = new Description();
		int currentInd = 0;
		int percentInd = -1;
		int maxVariable = 0;
		while ((percentInd = str.indexOf('%', currentInd)) != -1)
		{
			final String preText = str.substring(currentInd, percentInd);
			if (preText.length() > 0)
			{
				desc.addComponent(preText);
			}
			if (percentInd == str.length() - 1)
			{
				desc.addComponent("%");
				return desc;
			}
			if (str.charAt(percentInd + 1) == '{')
			{
				// This is a bracketed placeholder. The replacement parameter
				// is contained within the {}
				currentInd = str.indexOf('}', percentInd + 1) + 1;
				final String replacement =
						str.substring(percentInd + 1, currentInd);
				// For the time being we will only support numerics here.
				try
				{
					maxVariable =
							Math
								.max(maxVariable, Integer.parseInt(replacement));
				}
				catch (NumberFormatException nfe)
				{
					Logging
						.errorPrintLocalised(
							"Errors.Description.InvalidVariableReplacement", replacement); //$NON-NLS-1$
				}
				desc.addComponent(VAR_MARKER + replacement);
			}
			else if (str.charAt(percentInd + 1) == '%')
			{
				// This is an escape sequence so we can actually print a %
				currentInd = percentInd + 2;
				desc.addComponent("%"); //$NON-NLS-1$
			}
			else
			{
				// In this case we have an unbracketed placeholder. We will
				// walk the string until such time as we no longer have a number
				currentInd = percentInd + 1;
				StringBuffer numString = new StringBuffer();
				while (currentInd < str.length())
				{
					numString.append(str.charAt(currentInd));
					try
					{
						maxVariable =
								Math.max(maxVariable, Integer
									.parseInt(numString.toString()));
						currentInd++;
					}
					catch (NumberFormatException nfe)
					{
						break;
					}
				}
				if (currentInd > percentInd + 1)
				{
					desc.addComponent(VAR_MARKER
						+ str.substring(percentInd + 1, currentInd));
				}
				else
				{
					// We broke out of the variable finding loop without finding
					// even a single integer. Assume we have a DESC field that
					// is using a % unescaped.
					desc
						.addComponent(str.substring(percentInd, percentInd + 1));
				}
			}
		}
		desc.addComponent(str.substring(currentInd));
		finalizeConstruction(desc, maxVariable);
		return desc;
	}

	private static void finalizeConstruction(Description d, int maxVar)
	{
		d.trimToSize();
		d.setVariableCount(maxVar);
	}

}
