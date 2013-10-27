/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcgen.gui.filter;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
final class QuickFilter extends AbstractPObjectFilter
{

    private String query = null;

    QuickFilter()
    {
    }

    public String getQuery()
    {
	return query;
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    @Override
    public boolean accept(PlayerCharacter aPC, PObject pObject)
    {
	return query == null || (pObject.getDisplayName().toLowerCase().indexOf(query) >= 0 || pObject.getType().toLowerCase().indexOf(query) >= 0);
    }

}
