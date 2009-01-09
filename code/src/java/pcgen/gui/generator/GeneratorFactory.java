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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import pcgen.base.util.RandomUtil;
import pcgen.base.util.WeightedCollection;
import pcgen.gui.facade.InfoFacade;
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

    private static List<? extends StandardModeGenerator> buildStandardModeGeneratorList(Document document)
    {
        DocType type = document.getDocType();
        if (type.getSystemID().equals("StandardModeGenerator.dtd") &&
                type.getElementName().equals("GENERATORSET"))
        {
            Element root = document.getRootElement();
            boolean mutable = Boolean.parseBoolean(root.getAttributeValue("mutable"));
            List<StandardModeGenerator> generators = new ArrayList<StandardModeGenerator>();
            for (Object element : root.getChildren())
            {
                generators.add(buildStandardModeGenerator((Element) element,
                                                          mutable));
            }
            return generators;
        }
        return null;
    }

    private static StandardModeGenerator buildStandardModeGenerator(Element element,
                                                                      boolean mutable)
    {
        String name = element.getAttributeValue("name");
        boolean assignable = Boolean.parseBoolean(element.getAttributeValue("assignable"));
        @SuppressWarnings("unchecked")
        List<Element> children = element.getChildren();
        List<String> stats = new ArrayList<String>();
        for ( Element child : children)
        {
            stats.add(child.getText());
        }
        StandardModeGenerator generator = new DefaultStandardModeGenerator(name,
                                                                           assignable,
                                                                           stats);
        if (mutable)
        {
            return createMutableStandardModeGenerator(name, generator);
        }
        else
        {
            return generator;
        }
    }

    public static <T extends InfoFacade> MutableFacadeGenerator<T> createMutableFacadeGenerator(String name,
                                                                                                  FacadeGenerator<T> template)
    {
        MutableFacadeGenerator<T> generator = new DefaultMutableFacadeGenerator<T>(name);
        if (template != null)
        {
            for (T obj : template.getAll())
            {
                generator.add(obj);
            }
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
            generator.setAssignable(template.isAssignable());
            generator.setDiceExpressions(template.getDiceExpressions());
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

    private static class DefaultOrderedGenerator<E> extends AbstractGenerator<E>
    {

        protected List<E> items;
        private Queue<E> queue = null;
        protected boolean randomOrder;

        public DefaultOrderedGenerator(String name, List<E> items,
                                        boolean randomOrder)
        {
            super(name);
            this.items = items;
            this.randomOrder = randomOrder;
            reset();
        }

        public E getNext()
        {
            if (items.isEmpty())
            {
                return null;
            }
            if (queue.isEmpty())
            {
                reset();
            }
            return queue.poll();
        }

        public boolean isRandomOrder()
        {
            return randomOrder;
        }

        @Override
        public List<E> getAll()
        {
            return items;
        }

        protected Queue<E> createQueue()
        {
            LinkedList<E> temp = new LinkedList<E>(items);
            if (randomOrder)
            {
                Collections.shuffle(temp);
            }
            return temp;
        }

        @Override
        public void reset()
        {
            queue = createQueue();
        }

    }

    private static class DefaultFacadeGenerator<E extends InfoFacade> extends AbstractFacadeGenerator<E>
    {

        protected List<E> list;

        public DefaultFacadeGenerator(String name, List<E> items)
        {
            super(name);
            list = new Vector<E>(items);
        }

        public E getNext()
        {
            if (list.isEmpty())
            {
                return null;
            }
            return list.get(RandomUtil.getRandomInt(list.size()));
        }

        @Override
        public List<E> getAll()
        {
            return Collections.unmodifiableList(list);
        }

    }

    private static class DefaultMutableFacadeGenerator<E extends InfoFacade>
            extends DefaultFacadeGenerator<E> implements MutableFacadeGenerator<E>
    {

        @SuppressWarnings("unchecked")
        public DefaultMutableFacadeGenerator(String name)
        {
            super(name, Collections.EMPTY_LIST);
        }

        public void add(E element)
        {
            list.add(element);
        }

        public void remove(E element)
        {
            list.remove(element);
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

        protected boolean assignable;
        protected List<String> diceExpressions;

        public DefaultStandardModeGenerator(String name, boolean assignable,
                                             List<String> diceExpressions)
        {
            super(name);
            this.assignable = assignable;
            this.diceExpressions = diceExpressions;
        }

        public Integer getNext()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<String> getDiceExpressions()
        {
            return diceExpressions;
        }

        public boolean isAssignable()
        {
            return assignable;
        }

    }

    private static class DefaultMutableStandardModeGenerator extends DefaultStandardModeGenerator
            implements MutableStandardModeGenerator
    {

        @SuppressWarnings("unchecked")
        public DefaultMutableStandardModeGenerator(String name)
        {
            super(name, false, Collections.EMPTY_LIST);
        }

        public void setDiceExpressions(List<String> expressions)
        {
            diceExpressions = expressions;
        }

        public void setAssignable(boolean assign)
        {
            assignable = assign;
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

        public boolean isSingleton()
        {
            return false;
        }

        public Set<String> getSources()
        {
            Set<String> sources = new HashSet<String>();
            for (SkillFacade skill : priorityMap.keySet())
            {
                sources.add(skill.getSource());
            }
            return sources;
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
