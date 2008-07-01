/*
 * GenericListModel.java
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
 * Created on Jun 30, 2008, 9:32:17 PM
 */
package pcgen.gui.util;

import java.util.Collection;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GenericListModel<E> extends Vector<E> implements ListModel
{

    protected EventListenerList listenerList = new EventListenerList();

    public int getSize()
    {
        return size();
    }

    public E getElementAt(int index)
    {
        return elementAt(index);
    }

    /**
     * Sets the size of this list. 
     *
     * @param   newSize   the new size of this list
     * @see Vector#setSize(int)
     */
    @Override
    public void setSize(int newSize)
    {
        int oldSize = size();
        super.setSize(newSize);
        if (oldSize > newSize)
        {
            fireIntervalRemoved(this, newSize, oldSize - 1);
        }
        else if (oldSize < newSize)
        {
            fireIntervalAdded(this, oldSize, newSize - 1);
        }
    }

    /**
     * Sets the component at the specified <code>index</code> of this 
     * list to be the specified object. The previous component at that 
     * position is discarded.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index 
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>set(int,Object)</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      obj     what the component is to be set to
     * @param      index   the specified index
     * @see #set(int,Object)
     * @see Vector#setElementAt(Object,int)
     */
    @Override
    public void setElementAt(E obj, int index)
    {
        super.setElementAt(obj, index);
        fireContentsChanged(this, index, index);
    }

    /**
     * Deletes the component at the specified index.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index 
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>remove(int)</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      index   the index of the object to remove
     * @see #remove(int)
     * @see Vector#removeElementAt(int)
     */
    @Override
    public void removeElementAt(int index)
    {
        super.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }

    /**
     * Inserts the specified object as a component in this list at the 
     * specified <code>index</code>.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index 
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>add(int,Object)</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      obj     the component to insert
     * @param      index   where to insert the new component
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid
     * @see #add(int,Object)
     * @see Vector#insertElementAt(Object,int)
     */
    @Override
    public void insertElementAt(E obj, int index)
    {
        super.insertElementAt(obj, index);
        fireIntervalAdded(this, index, index);
    }

    @Override
    public boolean add(E o)
    {
        addElement(o);
        return true;
    }

    /**
     * Adds the specified component to the end of this list. 
     *
     * @param   obj   the component to be added
     * @see Vector#addElement(Object)
     */
    @Override
    public void addElement(E obj)
    {
        int index = size();
        super.addElement(obj);
        fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument 
     * from this list.
     *
     * @param   obj   the component to be removed
     * @return  <code>true</code> if the argument was a component of this
     *          list; <code>false</code> otherwise
     * @see Vector#removeElement(Object)
     */
    @Override
    public boolean removeElement(Object obj)
    {
        int index = indexOf(obj);
        boolean rv = super.removeElement(obj);
        if (index >= 0)
        {
            fireIntervalRemoved(this, index, index);
        }
        return rv;
    }

    /**
     * Removes all components from this list and sets its size to zero.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>clear</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @see #clear()
     * @see Vector#removeAllElements()
     */
    @Override
    public void removeAllElements()
    {
        int index1 = size() - 1;
        super.removeAllElements();
        if (index1 >= 0)
        {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    /* The remaining methods are included for compatibility with the
     * Java 2 platform Vector class.
     */
    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code>
     * if the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index index of element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
    @Override
    public E set(int index, E element)
    {
        E rv = elementAt(index);
        super.setElementAt(element, index);
        fireContentsChanged(this, index, index);
        return rv;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the
     * index is out of range
     * (<code>index &lt; 0 || index &gt; size()</code>).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
    @Override
    public void add(int index, E element)
    {
        super.insertElementAt(element, index);
        fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the element at the specified position in this list.
     * Returns the element that was removed from the list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code>
     * if the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index the index of the element to removed
     */
    @Override
    public E remove(int index)
    {
        E rv = elementAt(index);
        super.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
        return rv;
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns (unless it throws an exception).
     */
    @Override
    public void clear()
    {
        int index1 = size() - 1;
        super.removeAllElements();
        if (index1 >= 0)
        {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    /**
     * Deletes the components at the specified range of indexes.
     * The removal is inclusive, so specifying a range of (1,5)
     * removes the component at index 1 and the component at index 5,
     * as well as all components in between.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code>
     * if the index was invalid.
     * Throws an <code>IllegalArgumentException</code> if
     * <code>fromIndex &gt; toIndex</code>.
     *
     * @param      fromIndex the index of the lower end of the range
     * @param      toIndex   the index of the upper end of the range
     * @see	   #remove(int)
     */
    @Override
    public void removeRange(int fromIndex, int toIndex)
    {
        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = toIndex; i >= fromIndex; i--)
        {
            super.removeElementAt(i);
        }
        fireIntervalRemoved(this, fromIndex, toIndex);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        int index = size();
        boolean added = super.addAll(c);
        if (added)
        {
            fireIntervalAdded(this, index, index + c.size() - 1);
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        boolean added = super.addAll(index, c);
        if (added)
        {
            fireIntervalAdded(this, index, index + c.size() - 1);
        }
        return added;
    }

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be added
     */
    public void addListDataListener(ListDataListener l)
    {
        listenerList.add(ListDataListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a 
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */
    public void removeListDataListener(ListDataListener l)
    {
        listenerList.remove(ListDataListener.class, l);
    }

    /**
     * Returns an array of all the list data listeners
     * registered on this <code>AbstractListModel</code>.
     *
     * @return all of this model's <code>ListDataListener</code>s,
     *         or an empty array if no list data listeners
     *         are currently registered
     * 
     * @see #addListDataListener
     * @see #removeListDataListener
     * 
     * @since 1.4
     */
    public ListDataListener[] getListDataListeners()
    {
        return (ListDataListener[]) listenerList.getListeners(
                ListDataListener.class);
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements of the list change.  The changed elements
     * are specified by the closed interval index0, index1 -- the endpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED,
                                          index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements are added to the model.  The new elements
     * are specified by a closed interval index0, index1 -- the enpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED,
                                          index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model. 
     * <code>index0</code> and <code>index1</code> are the end points
     * of the interval that's been removed.  Note that <code>index0</code>
     * need not be less than or equal to <code>index1</code>.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval,
     *               including <code>index0</code>
     * @param index1 the other end of the removed interval,
     *               including <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED,
                                          index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a list model
     * <code>m</code>
     * for its list data listeners
     * with the following code:
     *
     * <pre>ListDataListener[] ldls = (ListDataListener[])(m.getListeners(ListDataListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getListDataListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType)
    {
        return listenerList.getListeners(listenerType);
    }

}
