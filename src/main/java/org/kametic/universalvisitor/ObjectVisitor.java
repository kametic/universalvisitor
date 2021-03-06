/**
 * Copyright (C) 2014 Kametic <epo.jemba@kametic.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * or any later version
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kametic.universalvisitor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.kametic.universalvisitor.api.Job;
import org.kametic.universalvisitor.api.MapReduce;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Metadata;
import org.kametic.universalvisitor.api.Node;
import org.kametic.universalvisitor.api.Reducer;
import org.kametic.universalvisitor.api.Visitor;
import org.kametic.universalvisitor.core.ChainedNode;
import org.kametic.universalvisitor.core.JobDefault;
import org.kametic.universalvisitor.core.MapReduceDefault;
import org.kametic.universalvisitor.core.object.FieldFilter;

/**
 * UniversalVisitor is the main entrypoint. With it you can visit any object graph instance.
 * <p>
 * It will first visit the object graph then produces a linked list of {@link Node}.
 * <p>
 * The Map Reduce pattern will then be applied from this linked list. Users can create their Map Reduce jobs by using the API provided.
 * <ul>
 *    <li> {@link Mapper} : a mapper from the Map Reduce design pattern
 *    <li> {@link Reducer} : a reducer from the Map Reduce design pattern
 *    <li> {@link Job} : a coherent set of one {@link Mapper} and one or more {@link Reducer}s
 *    <li> {@link MapReduce} : a coherent set of one {@link Mapper} and one or more {@link Reducer}s
 *    <li> {@link Job} : a set of {@link MapReduce}
 *  </ul>
 * 
 * 
 * @author Epo Jemba
 * @author Pierre Thirouin
 */
public class ObjectVisitor implements Visitor<Object,FieldFilter>
{

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Mapper)
     */
    @Override
    public <T> void visit(Object o, Mapper<T> mapper)
    {
        visit(o ,(FieldFilter) null  ,  mapper);
    }

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Filter, org.kametic.universalvisitor.api.Mapper)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void visit(Object o, FieldFilter filter, Mapper<T> mapper)
    {
        visit(o, filter, new MapReduceDefault<T>(mapper));
    }

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Mapper, org.kametic.universalvisitor.api.Reducer)
     */
    @Override
    public <T> void visit(Object o, Mapper<T> mapper, Reducer<T,?>... reducers)
    {
        visit(o , null , mapper, reducers);
    }

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Filter, org.kametic.universalvisitor.api.Mapper, org.kametic.universalvisitor.api.Reducer)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> void visit(Object o, FieldFilter filter, Mapper<T> mapper, Reducer<T,?>... reducers)
    {
        visit(o, filter,  new MapReduceDefault<T>(mapper, reducers));
    }

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.MapReduce)
     */
    @Override
    public <T> void visit(Object o, MapReduce<T>... mapReduces)
    {
        visit(o, null, mapReduces);
    }



    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Filter, org.kametic.universalvisitor.api.MapReduce)
     */
    @Override
    public <T> void visit(Object o, FieldFilter filter, MapReduce<T>... mapReduces)
    {
        visit(o, filter, new JobDefault<T,Object>(mapReduces));
    }



    /**
     * @param job
     * @param node
     * @return
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void doMapReduce(Job<?> job, ChainedNode node)
    {
        for (node = node.next(); node != null; node = node.next())
        {
            for (MapReduce mapReduce : job.mapReduces())
            {
                if (mapReduce.getMapper().handle(node.visitedElement()))
                {
                    Object t = mapReduce.getMapper().map(node);

                    for (Reducer<Object, Object> reducer : mapReduce.getReducers())
                    {
                        reducer.collect(t);
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Job)
     */
    @Override
    public void visit(Object o,  Job<?> job)
    {
         visit(o , null , job);
    }
    
    /* (non-Javadoc)
     * @see org.kametic.universalvisitor.Visitor#visit(java.lang.Object, org.kametic.universalvisitor.api.Filter, org.kametic.universalvisitor.api.Job)
     */
    @Override
    public void visit(Object o, FieldFilter filter, Job<?> job)
    {
        
        Set<Object> cache = new HashSet<Object>();
        
        ChainedNode node = ChainedNode.createRoot();
        
        FieldFilter f = null;
        
        if (filter == null)
        {
            f = FieldFilter.TRUE;
        }
        else
        {
            f = filter;
        }
        
        recursiveVisit(o, cache, node,  f);
        
        doMapReduce(job, node);
        
    }

  

  

    private void recursiveVisit(Object object, Set<Object> cache, ChainedNode node, FieldFilter filter)
    {

        int currentLevel = node.level() + 1;

        if (!cache.contains(object))
        {

            cache.add(object);

            if (object == null)
            {
                // ignore nulls
            }
            else if (Collection.class.isAssignableFrom(object.getClass()))
            {
                visitAllCollection((Collection<?>) object, cache, node, currentLevel, filter);
            }
            else if (object.getClass().isArray())
            {
                visitAllArray(object, cache, node, currentLevel, filter);
            }
            else if (Map.class.isAssignableFrom(object.getClass()))
            {
                visitAllMap((Map<?, ?>) object, cache, node, currentLevel, filter);
            }
            else
            {
                visitObject(object, cache, node, currentLevel, filter);
            }
        }
    }

    private void visitObject(Object object, Set<Object> cache, ChainedNode node, int currentLevel, FieldFilter filter)
    {
        visitObject(object, cache, node, currentLevel, filter, null);
    }

   

    private void visitObject(Object object, Set<Object> cache, ChainedNode node, int currentLevel, FieldFilter filter, Metadata metadata)
    {

        Class<? extends Object> currentClass = object.getClass();

        if (!isJdkMember(currentClass))
        {

            ChainedNode current = node;
            Class<?>[] family = getAllInterfacesAndClasses(currentClass);
            for (Class<?> elementClass : family)
            { // We iterate over all the family tree of the current class
              //
                if (elementClass != null && !isJdkMember(elementClass))
                {

                    for (Constructor<?> c : elementClass.getDeclaredConstructors())
                    {
                        if (!isJdkMember(c) && !c.isSynthetic())
                        {
                            current = current.append(object, c, currentLevel, metadata);
                        }
                    }
                    //
                    for (Method m : elementClass.getDeclaredMethods())
                    {
                        if (!isJdkMember(m) && !m.isSynthetic())
                        {
                            current = current.append(object, m, currentLevel, metadata);
                        }
                    }

                    for (Field f : elementClass.getDeclaredFields())
                    {
                        if (!isJdkMember(f) && !f.isSynthetic())
                        {

                            current = current.append(object, f, currentLevel, metadata);

                            if (filter != null && filter.retains(f))
                            {
                                Object deeperObject = readField(f, object);

                                recursiveVisit(deeperObject, cache, current, filter);
                                current = current.last();
                            }
                        }
                    }
                }

            }
        }
    }

    private void visitAllCollection(Collection<?> collection, Set<Object> cache, ChainedNode node, int currentLevel, FieldFilter filter)
    {
        ChainedNode current = node;

        boolean indexable = collection instanceof List || collection instanceof Queue;

        Object[] valArray = collection.toArray();
        for (int i = 0; i < valArray.length; i++)
        {
            Object value = valArray[i];
            if (value != null)
            {
                if (indexable)
                {
                    visitObject(value, cache, current, currentLevel, filter, new Metadata(i));
                }
                else
                {
                    visitObject(value, cache, current, currentLevel, filter);
                }
                current = current.last();
            }
        }
    }

    private void visitAllArray(Object arrayObject, Set<Object> cache, ChainedNode node, int currentLevel, FieldFilter filter)
    {
        ChainedNode current = node;

        int l = Array.getLength(arrayObject);
        for (int i = 0; i < l; i++)
        {
            Object value = Array.get(arrayObject, i);
            if (value != null)
            {
                visitObject(value, cache, current, currentLevel, filter, new Metadata(i));
                current = current.last();
            }
        }
    }

    private void visitAllMap(Map<?, ?> values, Set<Object> cache, ChainedNode pair, int currentLevel, FieldFilter filter)
    {
        ChainedNode current = pair;
        for (Object thisKey : values.keySet())
        {
            Object value = values.get(thisKey);
            if (value != null)
            {
                visitObject(thisKey, cache, current, currentLevel, filter);
                current = current.last();
                visitObject(value, cache, current, currentLevel, filter, new Metadata(thisKey));
                current = current.last();
            }
        }
    }

    private boolean isJdkMember(Member input)
    {
        return isJdkMember(input.getDeclaringClass());
    }

    private boolean isJdkMember(Class<?> input)
    {
        return input.getPackage().getName().startsWith("java.") || input.getPackage().getName().startsWith("javax.");
    }

    private Object readField(Field f, Object instance)
    {
        Object o = null;
        try
        {
            f.setAccessible(true);
            o = f.get(instance);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return o;
    }

    /**
     * Returns all the interfaces and classes implemented or extended by a class.
     *
     * @param clazz
     *            The class to search from.
     * @return The array of classes and interfaces found.
     */
    private Class<?>[] getAllInterfacesAndClasses(Class<?> clazz)
    {
        return getAllInterfacesAndClasses(new Class[] {
            clazz
        });
    }

    /**
     * This method walks up the inheritance hierarchy to make sure we get every class/interface extended or
     * implemented by classes.
     *
     * @param classes
     *            The classes array used as search starting point.
     * @return the found classes and interfaces.
     */
    @SuppressWarnings("unchecked")
    private Class<?>[] getAllInterfacesAndClasses(Class<?>[] classes)
    {
        if (0 == classes.length)
        {
            return classes;
        }
        else
        {
            List<Class<?>> extendedClasses = new ArrayList<Class<?>>();
            // all interfaces hierarchy
            for (Class<?> clazz : classes)
            {
                if (clazz != null)
                {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces != null)
                    {
                        extendedClasses.addAll(Arrays.asList(interfaces));
                    }
                    Class<?> superclass = clazz.getSuperclass();
                    if (superclass != null && superclass != Object.class)
                    {
                        extendedClasses.addAll(Arrays.asList(superclass));
                    }
                }
            }

            // Class::getInterfaces() gets only interfaces/classes
            // implemented/extended directly by a given class.
            // We need to walk the whole way up the tree.
            return concat(classes, getAllInterfacesAndClasses(extendedClasses.toArray(new Class[extendedClasses.size()])));
        }
    }

    @SuppressWarnings("rawtypes")
    private Class[] concat(Class[] A, Class[] B)
    {
        int aLen = A.length;
        int bLen = B.length;
        Class[] C = new Class[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

}
