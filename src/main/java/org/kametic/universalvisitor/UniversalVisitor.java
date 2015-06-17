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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.kametic.universalvisitor.api.Filter;
import org.kametic.universalvisitor.api.Job;
import org.kametic.universalvisitor.api.MapReduce;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Metadata;
import org.kametic.universalvisitor.api.Node;
import org.kametic.universalvisitor.api.Reducer;
import org.kametic.universalvisitor.core.JobDefault;
import org.kametic.universalvisitor.core.MapReduceDefault;
import org.kametic.universalvisitor.core.NodeDefault;

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
public class UniversalVisitor
{

    @SuppressWarnings("unchecked")
    public <T> void visit(AnnotatedElement ae, Mapper<T> mapper)
    {
        visit(ae, (Filter) null, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings("unchecked")
    public <T> void visit(Object o, Mapper<T> mapper)
    {
        visit(o, (Filter) null, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings("unchecked")
    public <T> void visit(AnnotatedElement o, Filter filter, Mapper<T> mapper)
    {
        visit(o, filter, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings("unchecked")
    public <T> void visit(Object o, Filter filter, Mapper<T> mapper)
    {
        visit(o, filter, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public <T> void visit(Object o, Mapper<T> mapper, Reducer... reducers)
    {
        visit(o, (Filter) null, new MapReduceDefault<T>(mapper, reducers));
    }

    @SuppressWarnings("unchecked")
    public <T> void visit(Object o, Filter filter, Mapper<T> mapper, Reducer<T, ?> reducer)
    {
        visit(o, filter, new MapReduceDefault<T>(mapper, reducer));
    }

    public void visit(Object o, MapReduce<?>... mapReduces)
    {
        visit(o, null, mapReduces);
    }

    @SuppressWarnings("rawtypes")
    public void visit(AnnotatedElement ae, Filter filter, MapReduce<?>... mapReduces)
    {
        visit(ae, filter, new JobDefault(mapReduces));
    }

    @SuppressWarnings({
        "rawtypes"
    })
    public void visit(Object o, Filter filter, MapReduce... mapReduces)
    {
        visit(o, filter, new JobDefault(mapReduces));
    }

	@SuppressWarnings({ "rawtypes" })
    public void visit(AnnotatedElement ae, Filter filter, Job job){
        Set<Object> cache = new HashSet<Object>();
		//ChainedNode Changed to linkedList<LocalNode> (perf issues when accessing last element fix)
		Queue<LocalNode> nodes = new LinkedList<UniversalVisitor.LocalNode>();
        if (filter == null)
        {
            filter = Filter.TRUE;
        }
		//The currentLevel is set at "-1" before the recursive instead of creating a dummy Node
		recursiveVisit(ae, cache, nodes, filter, -1);
		doMapReduce(job, nodes);
    }

    /**
     * @param job
     * @param node
     * @return
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
	private void doMapReduce(Job<?> job, Queue<LocalNode> nodes) 
	{
		for (LocalNode node : nodes) 
		{			
            for (MapReduce mapReduce : job.mapReduces())
            {
                if (mapReduce.getMapper().handle(node.annotatedElement()))
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

    @SuppressWarnings({
        "rawtypes"
    })
    public void visit(Object o, Filter filter, Job job)
    {

        Set<Object> cache = new HashSet<Object>();
		//Changed to linkedList of LocalNode instead of the ChainedNode (perf issues fix)
		Queue<LocalNode> nodes = new LinkedList<UniversalVisitor.LocalNode>();

        if (filter == null)
        {
            filter = Filter.TRUE;
        }

		recursiveVisit(o, cache, nodes, filter, -1);

		doMapReduce(job, nodes);

    }

	//ChainedNode changed to localNode due to performance issues
	private static class LocalNode extends NodeDefault 
	{

		protected LocalNode(Object instance, AnnotatedElement annotatedElement, int level) 
		{
            super(instance, annotatedElement, level);
        }



        @Override
		public LocalNode metadata(Metadata metadata) 
        {
			return (LocalNode) super.metadata(metadata);
        }


        @Override
        public String toString()
        {
            String indentation = "";
            for (int i = 0; i < level; i++)
            {
                indentation += "\t";
            } // instance()=
            String rep = String.format(
                    "%sLocalNode [ %s@%s , level=%s , annotatedElement=%s] \n",
                    indentation,
                    instance().getClass().getSimpleName(),
                    Integer.toHexString(instance().hashCode()),
                    level(),
                    annotatedElement());
            return rep;

            // return "ChainedNode [instance()=" + instance() + ", level()="
            // + level() + ", annotatedElement()=" + annotatedElement()
            // + "]  ==> \n" + next;
        }

    }

    // private void recursiveVisit(AnnotatedElement ae, Set<Object> cache, ChainedNode node, Filter filter)
    // {
    //
    // int currentLevel = node.level() + 1;
    //
    // if (!cache.contains(ae))
    // {
    //
    // cache.add(ae);
    //
    // if (ae == null)
    // {
    // // ignore nulls
    // }
    // else if (Constructor.class.isAssignableFrom(ae.getClass()))
    // {
    // visitConstructor((Constructor) ae, cache, node, currentLevel, filter);
    // }
    // else if (Method.class.isAssignableFrom(ae.getClass()))
    // {
    // visitMethod((Method) ae, cache, node, currentLevel, filter);
    // }
    // else if (Field.class.isAssignableFrom(ae.getClass()))
    // {
    // visitField((Field) ae, cache, node, currentLevel, filter);
    // }
    // else if (Package.class.isAssignableFrom(ae.getClass()))
    // {
    // visitPackage((Package) ae, cache, node, currentLevel, filter);
    // }
    // else if (Class.class.isAssignableFrom(ae.getClass()) && ae.getClass().isAnnotation())
    // {
    // visitClass((Constructor) ae, cache, node, currentLevel, filter);
    // }
    //
    // else
    // {
    // // visitObject(object, cache, node, currentLevel,filter);
    // throw new IllegalStateException("Can not visist " + ae);
    // }
    // }
    // }

	private void recursiveVisit(Object object, Set<Object> cache, Queue<LocalNode> nodes, Filter filter, int currentLevel) 
    {
		currentLevel ++;
        if (!cache.contains(object))
        {

            cache.add(object);

            if (object == null)
            {
                // ignore nulls
            }
            else if (Collection.class.isAssignableFrom(object.getClass()))
            {
                visitAllCollection((Collection<?>) object, cache, nodes, currentLevel, filter);
            }
            else if (object.getClass().isArray())
            {
                visitAllArray(object, cache, nodes, currentLevel, filter);
            }
            else if (Map.class.isAssignableFrom(object.getClass()))
            {
                visitAllMap((Map<?, ?>) object, cache, nodes, currentLevel, filter);
            }
            else
            {
                visitObject(object, cache, nodes, currentLevel, filter);
            }
        }
    }

	private void visitObject(Object object, Set<Object> cache, Queue<LocalNode> nodes, int currentLevel, Filter filter) 
    {
		visitObject(object, cache, nodes, currentLevel, filter, null);
    }

    // private <T> void visitConstructor(Constructor<T> ae, Set<Object> cache, ChainedNode node, int
    // currentLevel, Filter filter, Metadata metadata)
    // {
    // // Params
    // // Annotations
    // // Exceptions
    // Class<? extends Object> currentClass = ae.getClass();
    //
    // ChainedNode current = node;
    //
    // Class<?>[] family = getAllInterfacesAndClasses(currentClass);
    // for (Class<?> elementClass : family)
    // { // We iterate over all the family
    // // tree of the current class
    // //
    // if (elementClass != null && !isJdkMember(elementClass))
    // {
    //
    // for (Constructor<?> c : elementClass.getDeclaredConstructors())
    // {
    // if (!isJdkMember(c) && !c.isSynthetic())
    // {
    // current = current.append(ae, c, currentLevel, metadata);
    // }
    // }
    // //
    // for (Method m : elementClass.getDeclaredMethods())
    // {
    // if (!isJdkMember(m) && !m.isSynthetic())
    // {
    // current = current.append(ae, m, currentLevel, metadata);
    // }
    // }
    //
    // for (Field f : elementClass.getDeclaredFields())
    // {
    // if (!isJdkMember(f) && !f.isSynthetic())
    // {
    //
    // current = current.append(ae, f, currentLevel, metadata);
    //
    // if (filter != null && filter.retains(f))
    // {
    // Object deeperObject = readField(f, ae);
    //
    // recursiveVisit(deeperObject, cache, current, filter);
    // current = current.last();
    // }
    // }
    // }
    // }
    //
    // }
    // }

	private void visitObject(Object object, Set<Object> cache, Queue<LocalNode> nodes, int currentLevel, Filter filter, Metadata metadata)
	{

        Class<? extends Object> currentClass = object.getClass();

        if (!isJdkMember(currentClass))
        {

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
							nodes.add(new LocalNode(object, c, currentLevel).metadata(metadata));
                        }
                    }
                    //
                    for (Method m : elementClass.getDeclaredMethods())
                    {
                        if (!isJdkMember(m) && !m.isSynthetic())
                        {
							nodes.add(new LocalNode(object, m, currentLevel).metadata(metadata));
                        }
                    }

                    for (Field f : elementClass.getDeclaredFields())
                    {
                        if (!isJdkMember(f) && !f.isSynthetic())
                        {
							nodes.add(new LocalNode(object, f, currentLevel).metadata(metadata));


                            if (filter != null && filter.retains(f))
                            {
                                Object deeperObject = readField(f, object);

								recursiveVisit(deeperObject, cache, nodes, filter, currentLevel);
                            }
                        }
                    }
                }

            }
        }
    }

	private void visitAllCollection(Collection<?> collection, Set<Object> cache, Queue<LocalNode> nodes, int currentLevel, Filter filter) 
	{

        boolean indexable = collection instanceof List || collection instanceof Queue;

        Object[] valArray = collection.toArray();
        for (int i = 0; i < valArray.length; i++)
        {
            Object value = valArray[i];
            if (value != null)
            {
                if (indexable)
                {
					visitObject(value, cache, nodes, currentLevel, filter, new Metadata(i));
                }
                else
                {
					visitObject(value, cache, nodes, currentLevel, filter);
                }
            }
        }
    }

	private void visitAllArray(Object arrayObject, Set<Object> cache, Queue<LocalNode> nodes, int currentLevel, Filter filter) 
    {
        int l = Array.getLength(arrayObject);
        for (int i = 0; i < l; i++)
        {
            Object value = Array.get(arrayObject, i);
            if (value != null)
            {
				visitObject(value, cache, nodes, currentLevel, filter, new Metadata(i));
            }
        }
    }

	private void visitAllMap(Map<?, ?> values, Set<Object> cache, Queue<LocalNode> nodes, int currentLevel, Filter filter) 
    {
        for (Object thisKey : values.keySet())
        {
            Object value = values.get(thisKey);
            if (value != null)
            {
				visitObject(thisKey, cache, nodes, currentLevel, filter);
				visitObject(value, cache, nodes, currentLevel, filter, new Metadata(thisKey));
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
