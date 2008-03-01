/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcgen.gui.util.event;

import java.util.EventListener;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public interface TreeViewModelListener<T> extends EventListener
{

    public void dataChanged(TreeViewModelEvent<T> event);

}
