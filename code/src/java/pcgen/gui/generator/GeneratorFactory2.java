/*
 * GeneratorFactory2.java
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
 * Created on Jan 19, 2009, 6:35:13 PM
 */
package pcgen.gui.generator;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.Gender;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.AlignmentFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.ability.AbilityBuild;
import pcgen.gui.generator.stat.PurchaseModeGenerator;
import pcgen.gui.generator.stat.StandardModeGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class GeneratorFactory2
{

    private final DataSetFacade data;

    public GeneratorFactory2(DataSetFacade data)
    {
        this.data = data;
    }

    public static void main(String[] args)
    {
        StandardModeGenerator generator = new DefaultStandardModeGenerator("Test");
        generator.setAssignable(false);
        List<String> expressions = Arrays.asList("3d1", "3d2", "3d3", "3d4",
                                                 "3d5", "3d6");
//        expressions.add("3d1");
//        expressions.add("3d2");
//        expressions.add("3d3");
//        expressions.add("3d4");
//        expressions.add("3d5");
//        expressions.add("3d6");
        generator.setDiceExpressions(expressions);
        XMLEncoder encoder = new XMLEncoder(System.out);
        encoder.setPersistenceDelegate(DefaultStandardModeGenerator.class,
                                       new DefaultPersistenceDelegate(new String[]{"name"}));
        encoder.writeObject(generator);
        encoder.close();
    }

    private class CharacterBuild
    {

        public CharacterBuild(Element element, boolean mutable)
        {

        }
//
//        public Generator<AlignmentFacade> getAlignmentGenerator()
//        {
//            
//        }
//
//        public Generator<Gender> getGenderGenerator()
//        {
//
//        }
//
//        public Generator<Integer> getStatGenerator()
//        {
//
//        }
//
//        public InfoFacadeGenerator<RaceFacade> getRaceGenerator()
//        {
//
//        }
//
//        public InfoFacadeGenerator<ClassFacade> getClassGenerator()
//        {
//
//        }
//
//        public InfoFacadeGenerator<SkillFacade> getSkillGenerator()
//        {
//
//        }
//
//        public AbilityBuild getAbilityBuild()
//        {
//
//        }
    }

    private class BuildSet
    {

        private final List<Generator<AlignmentFacade>> alignmentGenerators;
        private final List<InfoFacadeGenerator<ClassFacade>> classGenerators;
        private final List<InfoFacadeGenerator<RaceFacade>> raceGenerators;

        public BuildSet(Document document) throws MissingDataException
        {
            this.alignmentGenerators = new ArrayList<Generator<AlignmentFacade>>();
            this.classGenerators = new ArrayList<InfoFacadeGenerator<ClassFacade>>();
            this.raceGenerators = new ArrayList<InfoFacadeGenerator<RaceFacade>>();
            init(document);
        }

        @SuppressWarnings("unchecked")
        private void init(Document document) throws MissingDataException
        {
            boolean mutable = false;
            if (document.getRootElement().getName().equals("BUILDSET"))
            {
                mutable = Boolean.parseBoolean(document.getRootElement().getAttributeValue("mutable"));
            }

            Iterator<Element> it = document.getDescendants(new ElementFilter("ALIGNMENT_GENERATOR"));
            while (it.hasNext())
            {
                alignmentGenerators.add(new DefaultAlignmentGenerator(it.next()));
            }
            it = document.getDescendants(new ElementFilter("RACE_GENERATOR"));
            while (it.hasNext())
            {
                raceGenerators.add(new DefaultRaceGenerator(it.next(), mutable));
            }
        }

        public List<Generator<AlignmentFacade>> getAlignmentGenerators()
        {
            return alignmentGenerators;
        }

        public List<InfoFacadeGenerator<ClassFacade>> getClassGenerators()
        {
            return classGenerators;
        }

        public List<InfoFacadeGenerator<RaceFacade>> getRaceGenerators()
        {
            return raceGenerators;
        }

    }

    public static abstract class AbstractGenerator<E> implements Generator<E>
    {

        private String name;
        private boolean mutable;
        protected Element element;

        public AbstractGenerator(String name)
        {
            this.name = name;
            this.mutable = true;
            element = null;
        }

        public AbstractGenerator(Element element, boolean mutable)
        {
            this.name = element.getAttributeValue("name");
            this.mutable = mutable;
            this.element = element;
        }

        public List<E> getAll()
        {
            return Collections.emptyList();
        }

        public void reset()
        {

        }

        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }

        public boolean isMutable()
        {
            return mutable;
        }

    }

    private static abstract class AbstractWeightedGenerator<E> extends AbstractGenerator<E>
            implements WeightedGenerator<E>
    {

        protected Map<E, Integer> priorityMap;
        private Queue<E> queue = null;

        public AbstractWeightedGenerator(Element element, boolean mutable) throws MissingDataException
        {
            super(element, mutable);
            @SuppressWarnings("unchecked")
            List<Element> children = element.getContent(
                    new ElementFilter("SOURCE")
                    {

                        @Override
                        public boolean matches(Object arg0)
                        {
                            return negate().matches(arg0);
                        }

                    });
            for (Element child : children)
            {
                String elementName = child.getText();
                Integer weight = Integer.valueOf(child.getAttributeValue("weight"));
                E facade = getFacade(elementName);
                if (facade == null)
                {
                    throw new MissingDataException(elementName +
                                                   " not found in DataSetFacade");
                }
                priorityMap.put(facade, weight);
            }
            queue = new LinkedList<E>();
        }

        protected abstract E getFacade(String name);

        @Override
        public final List<E> getAll()
        {
            return new ArrayList<E>(priorityMap.keySet());
        }

        public final E getNext()
        {
            if (priorityMap.isEmpty())
            {
                return null;
            }
            if (queue.isEmpty())
            {
                reset();
            }
            return queue.poll();
        }

        public final int getWeight(E item)
        {
            return priorityMap.get(item);
        }

        public final void setWeight(E item, int weight)
        {
            priorityMap.put(item, weight);
        }

        public boolean isSingleton()
        {
            return false;
        }

        @Override
        public final void reset()
        {
            queue = createQueue();
        }

        protected Queue<E> createQueue()
        {
            return new RandomWeightedQueue();
        }

        private class RandomWeightedQueue extends WeightedCollection<E>
                implements Queue<E>
        {

            private E element = null;

            public RandomWeightedQueue()
            {
                for (E skill : priorityMap.keySet())
                {
                    add(skill, priorityMap.get(skill));
                }
            }

            public boolean offer(E o)
            {
                return false;
            }

            public E poll()
            {
                if (isEmpty())
                {
                    return null;
                }
                return remove();
            }

            public E remove()
            {
                E skill = element();
                remove(skill);
                element = null;
                return skill;
            }

            public E peek()
            {
                if (isEmpty())
                {
                    return null;
                }
                return element();
            }

            public E element()
            {
                if (element == null)
                {
                    element = getRandomValue();
                }
                return element;
            }

        }
    }

    private abstract static class AbstractInfoFacadeGenerator<E extends InfoFacade>
            extends AbstractWeightedGenerator<E> implements InfoFacadeGenerator<E>
    {

        protected boolean randomOrder;

        @SuppressWarnings("unchecked")
        public AbstractInfoFacadeGenerator(Element element, boolean mutable) throws MissingDataException
        {
            super(element, mutable);
            this.randomOrder = element.getAttributeValue("type").equals("random");
        }

        public boolean isRandomOrder()
        {
            return randomOrder;
        }

        public void setRandomOrder(boolean randomOrder)
        {
            this.randomOrder = randomOrder;
        }

        public Set<String> getSources()
        {
            Set<String> sources = new HashSet<String>();
            for (E facade : getAll())
            {
                sources.add(facade.getSource());
            }
            return sources;
        }

        @Override
        protected Queue<E> createQueue()
        {
            if (randomOrder)
            {
                return super.createQueue();
            }
            else
            {
                Comparator<E> comparator = new Comparator<E>()
                {

                    public int compare(E o1,
                                        E o2)
                    {
                        // compare the numbers in reverse in order for the highest priority
                        // Skills to be used first
                        return priorityMap.get(o2).compareTo(priorityMap.get(o1));
                    }

                };
                Queue<E> queue = new PriorityQueue<E>(priorityMap.size(),
                                                      comparator);
                queue.addAll(priorityMap.keySet());
                return queue;
            }
        }

    }

    private class DefaultAlignmentGenerator extends AbstractWeightedGenerator<AlignmentFacade>
    {

        public DefaultAlignmentGenerator(Element element) throws MissingDataException
        {
            super(element, false);
        }

        @Override
        protected AlignmentFacade getFacade(String name)
        {
            return data.getGameMode().getAlignment(name);
        }

    }

    private class DefaultRaceGenerator extends AbstractInfoFacadeGenerator<RaceFacade>
    {

        public DefaultRaceGenerator(Element element, boolean mutable) throws MissingDataException
        {
            super(element, mutable);
        }

        @Override
        protected RaceFacade getFacade(String name)
        {
            return data.getRace(name);
        }

    }

    private class DefaultClassGenerator extends AbstractInfoFacadeGenerator<ClassFacade>
    {

        public DefaultClassGenerator(Element element, boolean mutable) throws MissingDataException
        {
            super(element, mutable);
        }

        @Override
        protected ClassFacade getFacade(String name)
        {
            return data.getClass(name);
        }

    }

    private static class DefaultPurchaseModeGenerator extends AbstractGenerator<Integer>
            implements PurchaseModeGenerator
    {

        protected Vector<Integer> costs;
        protected int points;
        protected int min;

        public DefaultPurchaseModeGenerator(Element element, boolean mutable)
        {
            super(element, mutable);
            this.points = Integer.parseInt(element.getAttributeValue("points"));
            TreeMap<Integer, Integer> costMap = new TreeMap<Integer, Integer>();
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren();
            for ( Element child : children)
            {
                Integer score = Integer.valueOf(child.getAttributeValue("score"));
                Integer cost = Integer.valueOf(child.getText());
                costMap.put(score, cost);
            }
            this.min = costMap.firstKey();
            this.costs = new Vector<Integer>(costMap.values());
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

    public static class DefaultStandardModeGenerator extends AbstractGenerator<Integer>
            implements StandardModeGenerator
    {

        protected boolean assignable;
        protected List<String> diceExpressions;

        public DefaultStandardModeGenerator(String name)
        {
            super(name);
            assignable = true;
            diceExpressions = Collections.emptyList();
        }

        public DefaultStandardModeGenerator(Element element, boolean mutable)
        {
            super(element, mutable);
            this.assignable = Boolean.parseBoolean(element.getAttributeValue("assignable"));
            this.diceExpressions = new ArrayList<String>();
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren();
            for ( Element child : children)
            {
                diceExpressions.add(child.getText());
            }
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

        public void setDiceExpressions(List<String> expressions)
        {
            diceExpressions = expressions;
        }

        public void setAssignable(boolean assign)
        {
            assignable = assign;
        }

    }

    private class DefaultSkillGenerator extends AbstractInfoFacadeGenerator<SkillFacade>
    {

        public DefaultSkillGenerator(Element element, boolean mutable) throws MissingDataException
        {
            super(element, mutable);
        }

        @Override
        protected SkillFacade getFacade(String name)
        {
            return data.getSkill(name);
        }

    }

    private class DefaultAbilityBuild implements AbilityBuild
    {

        private String name;
        protected Map<AbilityCatagoryFacade, DefaultAbilityGenerator> generatorMap;

        public DefaultAbilityBuild(Element element, boolean mutable) throws MissingDataException
        {
            this.name = element.getAttributeValue("name");
            this.generatorMap = new HashMap<AbilityCatagoryFacade, DefaultAbilityGenerator>();
            @SuppressWarnings("unchecked")
            List<Element> children = element.getChildren("GENERATOR");
            for ( Element element1 : children)
            {
                DefaultAbilityGenerator generator = new DefaultAbilityGenerator(element1,
                                                                                mutable);
                AbilityCatagoryFacade catagory = data.getAbilityCatagory(generator.toString());
                generatorMap.put(catagory, generator);
            }

        }

        public InfoFacadeGenerator<AbilityFacade> getGenerator(AbilityCatagoryFacade catagory)
        {
            return generatorMap.get(catagory);
        }

        @Override
        public String toString()
        {
            return name;
        }

    }

    private class DefaultAbilityGenerator extends AbstractInfoFacadeGenerator<AbilityFacade>
    {

        private String catagory;

        public DefaultAbilityGenerator(Element element,
                                        boolean mutable) throws MissingDataException
        {
            super(element,
                  mutable);
            this.catagory = element.getAttributeValue("catagory");
        }

        @Override
        protected AbilityFacade getFacade(String name)
        {
            return data.getAbility(data.getAbilityCatagory(catagory), name);
        }

        @Override
        public String toString()
        {
            return catagory;
        }

    }
}
