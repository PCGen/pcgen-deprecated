/*
 * GeneratorElement.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Feb 1, 2009, 2:59:19 PM
 */
package pcgen.gui.generator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.jdom.Element;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GeneratorElement extends Element
{

    private final EventListenerList listenerList;

    public GeneratorElement(String name)
    {
        super(name);
        listenerList = new EventListenerList();
    }

    public void addChangeListener(ChangeListener listener)
    {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener)
    {
        listenerList.remove(ChangeListener.class, listener);
    }

    public void fireChangeEvent()
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ChangeEvent event = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ChangeListener.class)
            {
                // Lazily create the event:
                if (event == null)
                {
                    event = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }

}
