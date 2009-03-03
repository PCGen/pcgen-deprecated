/*
 * AbstractGenericListDataWrapper.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jul 15, 2008, 8:14:59 PM
 */
package pcgen.gui.util.event;

import java.util.Collection;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;

/**
 * This class is only effective if the subclass does not call <code>getData()</code>
 * in its implementation of the <code>intervalAdded()</code> method. 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractGenericListDataWrapper<E> implements
		GenericListDataListener<E>
{

	protected GenericListModelWrapper<E> wrapper = null;

	public AbstractGenericListDataWrapper()
	{
	}

	public AbstractGenericListDataWrapper(GenericListModel<E> model)
	{
		setModel(model);
	}

	public void setModel(GenericListModel<E> model)
	{
		if (wrapper != null)
		{
			wrapper.getModel().removeGenericListDataListener(this);
			clearData();
		}
		if (model != null)
		{
			wrapper = new GenericListModelWrapper<E>(model);
			model.addGenericListDataListener(this);
			addData(wrapper);
		}
		else
		{
			wrapper = null;
		}
	}

	protected void clearData()
	{
		removeData(wrapper);
	}

	public void intervalAdded(GenericListDataEvent<E> e)
	{
		addData(e.getData());
	}

	public void intervalRemoved(GenericListDataEvent<E> e)
	{
		removeData(e.getData());
	}

	protected abstract void addData(Collection<? extends E> data);

	protected abstract void removeData(Collection<? extends E> data);

	public void contentsChanged(GenericListDataEvent<E> e)
	{
		intervalRemoved(e);
		intervalAdded(new GenericListDataEvent<E>(e.getSource(),
												  wrapper.subList(e.getIndex0(),
																  e.getIndex1() +
				1),
												  e.getValueIsAdjusting(),
												  e.getType(), e.getIndex0(),
												  e.getIndex1()));
	}

}
