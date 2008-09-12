/*
 * StatGeneratorFactory.java
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
 * Created on Sep 11, 2008, 5:22:51 PM
 */
package pcgen.gui.generator.stat;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import pcgen.gui.generator.AbstractGenerator;
import pcgen.gui.generator.DefaultOrderedGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class StatGeneratorFactory
{

    private StatGeneratorFactory()
    {
    }

    public static MutableAssignmentModeGenerator createMutableAssignmentModeGenerator(String name,
                                                                                        AssignmentModeGenerator template)
    {
        MutableAssignmentModeGenerator generator = new DefaultMutableAssignmentModeGenerator(name);
        if (template != null)
        {
            generator.setAssignable(template.isAssignable());
            generator.setScores(template.getAll());
        }
        return generator;
    }

    public static MutablePurchaseModeGenerator createMutablePurchaseModeGenerator(String name,
                                                                                    PurchaseModeGenerator template)
    {
        MutablePurchaseModeGenerator generator = new DefaultMutablePurchaseModeGenerator(name);
        if (template != null)
        {
            int min = template.getMinScore();
            int max = template.getMaxScore();
            generator.setMinScore(min);
            generator.setMaxScore(max);
            generator.setPoints(template.getPoints());
            for (int i = min; i <= max; i++)
            {
                generator.setScoreCost(i, template.getScoreCost(i));
            }
        }
        return generator;
    }

    public static MutableStandardModeGenerator createMutableStandardModeGenerator(String name,
                                                                                    StandardModeGenerator template)
    {
        MutableStandardModeGenerator generator = new DefaultMutableStandardModeGenerator(name);
        if (template != null)
        {
            generator.setDiceExpression(template.getDiceExpression());
            generator.setDropCount(template.getDropCount());
            generator.setRerollMinimum(template.getRerollMinimum());
        }
        return generator;
    }

    private static class DefaultAssignmentModeGenerator extends DefaultOrderedGenerator<Integer>
            implements AssignmentModeGenerator
    {

        protected boolean assignable;

        public DefaultAssignmentModeGenerator(String name, List<Integer> scores,
                                               boolean assignable)
        {
            super(name, scores, true);
            this.assignable = assignable;
        }

        public boolean isAssignable()
        {
            return assignable;
        }

    }

    private static class DefaultMutableAssignmentModeGenerator extends DefaultAssignmentModeGenerator
            implements MutableAssignmentModeGenerator
    {

        @SuppressWarnings("unchecked")
        public DefaultMutableAssignmentModeGenerator(String name)
        {
            super(name, Collections.EMPTY_LIST, false);
        }

        public void setScores(List<Integer> scores)
        {
            this.items = scores;
            reset();
        }

        public void setAssignable(boolean assignable)
        {
            this.assignable = assignable;
        }

    }

    private static class DefaultPurchaseModeGenerator extends AbstractGenerator<Integer>
            implements PurchaseModeGenerator
    {

        protected Vector<Integer> costs;
        protected int points;
        protected int min;

        protected DefaultPurchaseModeGenerator(String name)
        {
            super(name);
            this.points = 0;
            this.min = 0;
            this.costs = new Vector<Integer>();
        }

        public DefaultPurchaseModeGenerator(String name, int points, int min,
                                             List<Integer> costs)
        {
            super(name);
            this.points = points;
            this.min = min;
            this.costs = new Vector<Integer>(costs);
        }

        public Integer getNext()
        {
            return null;
        }

        public int getMinScore()
        {
            return min;
        }

        public int getMaxScore()
        {
            return min + costs.size() - 1;
        }

        public int getScoreCost(int score)
        {
            return costs.get(score - min);
        }

        public int getPoints()
        {
            return points;
        }

    }

    private static class DefaultMutablePurchaseModeGenerator extends DefaultPurchaseModeGenerator
            implements MutablePurchaseModeGenerator
    {

        public DefaultMutablePurchaseModeGenerator(String name)
        {
            super(name);
        }

        public void setMaxScore(int score)
        {
            costs.setSize(score - min + 1);
        }

        public void setMinScore(int score)
        {
            this.min = score;
        }

        public void setPoints(int points)
        {
            this.points = points;
        }

        public void setScoreCost(int score, int cost)
        {
            costs.set(score - min, cost);
        }

    }

    private static class DefaultStandardModeGenerator extends AbstractGenerator<Integer>
            implements StandardModeGenerator
    {

        protected String diceExpression;
        protected int dropCount;
        protected int rerollMinimum;

        public DefaultStandardModeGenerator(String name, String diceExpression,
                                             int dropCount,
                                             int rerollMinimum)
        {
            super(name);
            this.diceExpression = diceExpression;
            this.dropCount = dropCount;
            this.rerollMinimum = rerollMinimum;
        }

        public Integer getNext()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getDiceExpression()
        {
            return diceExpression;
        }

        public int getRerollMinimum()
        {
            return rerollMinimum;
        }

        public int getDropCount()
        {
            return dropCount;
        }

    }

    private static class DefaultMutableStandardModeGenerator extends DefaultStandardModeGenerator
            implements MutableStandardModeGenerator
    {

        public DefaultMutableStandardModeGenerator(String name)
        {
            super(name, null, 0, 0);
        }

        public void setDiceExpression(String expression)
        {
            this.diceExpression = expression;
        }

        public void setRerollMinimum(int min)
        {
            this.rerollMinimum = min;
        }

        public void setDropCount(int count)
        {
            this.dropCount = count;
        }

    }
}
