/*
 * GeneratorFactory.java
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
package pcgen.gui.generator;

import java.util.ArrayList;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.stat.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import pcgen.base.util.WeightedCollection;
import pcgen.gui.generator.skill.MutableSkillGenerator;
import pcgen.gui.generator.skill.SkillGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorFactory
{

    private GeneratorFactory()
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

    public static MutableSkillGenerator createMutableSkillGenerator(String name,
                                                                      SkillGenerator template)
    {
        MutableSkillGenerator generator = new DefaultMutableSkillGenerator(name);
        if (template != null)
        {
            generator.setRandomOrder(template.isRandomOrder());
            for (SkillFacade skill : template.getAll())
            {
                generator.setSkillPriority(skill,
                                           template.getSkillPriority(skill));
            }
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
            super(name, Collections.EMPTY_LIST, true);
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
            costs.add(0);
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

    private static class DefaultSkillGenerator extends DefaultOrderedGenerator<SkillFacade>
            implements SkillGenerator
    {

        protected Map<SkillFacade, Integer> priorityMap;

        public DefaultSkillGenerator(String name,
                                      Map<SkillFacade, Integer> priorityMap,
                                      boolean randomOrder)
        {
            super(name, new ArrayList<SkillFacade>(priorityMap.keySet()),
                  randomOrder);
            this.priorityMap = priorityMap;
        }

        public int getSkillPriority(SkillFacade skill)
        {
            return priorityMap.get(skill);
        }

        @Override
        protected Queue<SkillFacade> createQueue()
        {
            if (!randomOrder)
            {
                Comparator<SkillFacade> comparator = new Comparator<SkillFacade>()
                {

                    public int compare(SkillFacade o1,
                                        SkillFacade o2)
                    {
                        // compare the numbers in reverse in order for the highest priority
                        // Skills to be used first
                        return priorityMap.get(o2).compareTo(priorityMap.get(o1));
                    }

                };
                Queue<SkillFacade> queue = new PriorityQueue<SkillFacade>(priorityMap.size(),
                                                                          comparator);
                queue.addAll(priorityMap.keySet());
                return queue;
            }
            return new RandomWeightedQueue();
        }

        private class RandomWeightedQueue extends WeightedCollection<SkillFacade>
                implements Queue<SkillFacade>
        {

            private SkillFacade element = null;

            public RandomWeightedQueue()
            {
                for (SkillFacade skill : priorityMap.keySet())
                {
                    add(skill, priorityMap.get(skill));
                }
            }

            public boolean offer(SkillFacade o)
            {
                return false;
            }

            public SkillFacade poll()
            {
                if (isEmpty())
                {
                    return null;
                }
                return remove();
            }

            public SkillFacade remove()
            {
                SkillFacade skill = element();
                remove(skill);
                element = null;
                return skill;
            }

            public SkillFacade peek()
            {
                if (isEmpty())
                {
                    return null;
                }
                return element();
            }

            public SkillFacade element()
            {
                if (element == null)
                {
                    element = getRandomValue();
                }
                return element;
            }

        }
    }

    private static class DefaultMutableSkillGenerator extends DefaultSkillGenerator
            implements MutableSkillGenerator
    {

        public DefaultMutableSkillGenerator(String name)
        {
            super(name, new HashMap<SkillFacade, Integer>(), false);
            // This makes getNext() work correctly
            this.items = Collections.singletonList(null);
        }

        @Override
        public List<SkillFacade> getAll()
        {
            return new ArrayList<SkillFacade>(priorityMap.keySet());
        }

        public void setSkillPriority(SkillFacade skill, int priority)
        {
            priorityMap.put(skill, priority);
        }

        public void setRandomOrder(boolean randomOrder)
        {
            this.randomOrder = randomOrder;
        }

    }
}
