/*
 * PurchaseModePanel.java
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
 * Created on Sep 9, 2008, 2:23:58 PM
 */
package pcgen.gui.generator.stat;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Customizer;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class PurchaseModePanel extends JPanel implements Customizer
{

	private final String SCORE = ResourceManager.getText("score");
	private final String COST = ResourceManager.getText("cost");
	private final ScoreCostTableModel model;
	private final JSpinner pointSpinner;
	private final JSpinner minSpinner;
	private final JSpinner maxSpinner;
	private MutablePurchaseModeGenerator generator = null;

	public PurchaseModePanel()
	{
		super(new GridBagLayout());
		this.model = new ScoreCostTableModel();
		this.pointSpinner = new JSpinner();
		this.minSpinner = new JSpinner();
		this.maxSpinner = new JSpinner();
		initComponents();
	}

	private void initComponents()
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3;
		{//Initialize gridBagConstraints
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.insets = new Insets(2, 4, 2, 2);
		}
		{//Initialize gridBagConstraints2
			gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints2.ipadx = 10;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		}
		JTable table = new JTable(model)
		{

			@Override
			public boolean getScrollableTracksViewportHeight()
			{
				// fetch the table's parent
				Container viewport = getParent();
				// if the parent is not a viewport, calling this isn't useful
				if (!(viewport instanceof JViewport))
				{
					return false;
				}
				// return true if the table's preferred height is smaller
				// than the viewport height, else false
				return getPreferredSize().height < viewport.getHeight();
			}

		};
		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridheight = 5;
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.weighty = 1.0;
		gridBagConstraints3.insets = new Insets(2, 0, 2, 0);
		add(new JScrollPane(table), gridBagConstraints3);
		add(new JLabel(ResourceManager.getText("points") + ":"),
			gridBagConstraints);
		{//Initialize pointSpinner
			pointSpinner.setEnabled(false);
			pointSpinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
			pointSpinner.addChangeListener(model);
		}
		add(pointSpinner, gridBagConstraints2);

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.ipady = 1;
		gridBagConstraints3.insets = new Insets(2, 0, 2, 0);
		add(new JSeparator(), gridBagConstraints3);
		add(new JLabel(ResourceManager.getText("minScore") + ":"),
			gridBagConstraints);
		// Comparables are inserted into the SpinnerNumberModels to make sure
		// that min <= max is always true
		// It is nessesary to customize both the comparables and the spinnerModels
		// In order to trick swing into using a dynamic variable instead of a static one
		// TODO: There might be better ways to get the same result without doing this
		Comparable<Integer> comparable;
		SpinnerNumberModel spinnerModel;
		{
			comparable = new Comparable<Integer>()
			{

				public int compareTo(Integer o)
				{
					return model.getMax().compareTo(o);
				}

			};
			spinnerModel = new SpinnerNumberModel(0, 0, comparable, 1)
			{

				@Override
				public Comparable getMaximum()
				{
					return model.getMax();
				}

			};
			minSpinner.setEnabled(false);
			minSpinner.setModel(spinnerModel);
			minSpinner.addChangeListener(model);
		}
		add(minSpinner, gridBagConstraints2);
		add(new JLabel(ResourceManager.getText("maxScore") + ":"),
			gridBagConstraints);
		{
			comparable = new Comparable<Integer>()
			{

				public int compareTo(Integer o)
				{
					return model.getMin().compareTo(o);
				}

			};
			spinnerModel = new SpinnerNumberModel(0, comparable, null, 1)
			{

				@Override
				public Comparable getMinimum()
				{
					return model.getMin();
				}

			};
			maxSpinner.setEnabled(false);
			maxSpinner.setModel(spinnerModel);
			maxSpinner.addChangeListener(model);
		}
		add(maxSpinner, gridBagConstraints2);

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(), gridBagConstraints3);
	}

	public void setObject(Object bean)
	{
		if (!(bean instanceof PurchaseModeGenerator))
		{
			throw new IllegalArgumentException();
		}
		PurchaseModeGenerator generator = (PurchaseModeGenerator) bean;
		pointSpinner.setValue(generator.getPoints());

		model.setGenerator(generator);
		minSpinner.setValue(model.getMin());
		maxSpinner.setValue(model.getMax());

		boolean editable = generator instanceof MutablePurchaseModeGenerator;
		pointSpinner.setEnabled(editable);
		minSpinner.setEnabled(editable);
		maxSpinner.setEnabled(editable);

		this.generator = editable ? (MutablePurchaseModeGenerator) generator : null;
	}

	private class ScoreCostTableModel extends AbstractTableModel implements ChangeListener
	{

		private final Vector<Integer> costs = new Vector<Integer>();
		private int min = 0;
		private int max = 0;

		public ScoreCostTableModel()
		{
			costs.setSize(1);
		}

		public void setGenerator(PurchaseModeGenerator generator)
		{
			min = generator.getMinScore();
			max = generator.getMaxScore();
			costs.clear();
			for (int score = min; score <= max; score++)
			{
				costs.add(generator.getScoreCost(score));
			}
			fireTableDataChanged();
		}

		public int getRowCount()
		{
			return max - min + 1;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public Integer getMax()
		{
			return max;
		}

		public Integer getMin()
		{
			return min;
		}

		public void stateChanged(ChangeEvent e)
		{
			if (e.getSource() == pointSpinner)
			{
				generator.setPoints((Integer) pointSpinner.getValue());
			}
			else if (e.getSource() == minSpinner)
			{
				setMin((Integer) minSpinner.getValue());
				generator.setMinScore(min);
			}
			else
			{
				setMax((Integer) maxSpinner.getValue());
				generator.setMaxScore(max);
			}
		}

		public void setMax(int max)
		{
			int oldmax = this.max;
			this.max = max;
			if (oldmax < max)
			{
				int firstRow = oldmax - min + 1;
				int lastRow = max - min;
				costs.addAll(firstRow,
							 Collections.nCopies(lastRow - firstRow + 1, 0));
				fireTableRowsInserted(firstRow, lastRow);
			}
			else if (max < oldmax)
			{
				int firstRow = max - min + 1;
				int lastRow = oldmax - min;
				costs.subList(firstRow, lastRow + 1).clear();
				fireTableRowsDeleted(firstRow, lastRow);
			}
		}

		public void setMin(int min)
		{
			int oldmin = this.min;
			this.min = min;
			if (oldmin < min)
			{
				costs.subList(0, min - oldmin).clear();
				fireTableRowsDeleted(0, min - oldmin - 1);
			}
			else if (min < oldmin)
			{
				costs.addAll(0, Collections.nCopies(oldmin - min, 0));
				fireTableRowsInserted(0, oldmin - min - 1);
			}
		}

		@Override
		public String getColumnName(int column)
		{
			return column == 0 ? SCORE : COST;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return columnIndex == 0 ? Object.class : Integer.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return generator != null && columnIndex == 1;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return columnIndex == 0 ? rowIndex + min : costs.get(rowIndex);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			costs.set(rowIndex, (Integer) aValue);
			generator.setScoreCost(rowIndex + min, (Integer) aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}

	}
}
