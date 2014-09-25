package org.kametic.universalvisitor;

import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;

import org.kametic.universalvisitor.ObjectVisitor.ChainedNode;
import org.kametic.universalvisitor.api.Filter;
import org.kametic.universalvisitor.api.Job;
import org.kametic.universalvisitor.api.MapReduce;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.core.JobDefault;
import org.kametic.universalvisitor.core.MapReduceDefault;

public class Dummy
{
    @SuppressWarnings("unchecked")
    public <T> void visit(AnnotatedElement ae, Mapper<T> mapper)
    {
        visit(ae, (Filter) null, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings("unchecked")
    public <T> void visit(AnnotatedElement o, Filter filter, Mapper<T> mapper)
    {
        visit(o, filter, new MapReduceDefault<T>(mapper));
    }

    @SuppressWarnings("rawtypes")
    public void visit(AnnotatedElement ae, Filter filter, MapReduce<?>... mapReduces)
    {
        visit(ae, filter, new JobDefault(mapReduces));
    }

    @SuppressWarnings({
        "rawtypes"
    })
    public void visit(AnnotatedElement ae, Filter filter, Job job)
    {
        Set<Object> cache = new HashSet<Object>();
        ChainedNode node = ChainedNode.createRoot();
        if (filter == null)
        {
            filter = Filter.TRUE;
        }

        recursiveVisit(ae, cache, node, filter);
        doMapReduce(job, node);
    }

    private void recursiveVisit(AnnotatedElement ae, Set<Object> cache, ChainedNode node, Filter filter)
    {

        int currentLevel = node.level() + 1;

        if (!cache.contains(ae))
        {

            cache.add(ae);

            if (ae == null)
            {
                // ignore nulls
            }
            else if (Constructor.class.isAssignableFrom(ae.getClass()))
            {
                visitConstructor((Constructor) ae, cache, node, currentLevel, filter);
            }
            else if (Method.class.isAssignableFrom(ae.getClass()))
            {
                visitMethod((Method) ae, cache, node, currentLevel, filter);
            }
            else if (Field.class.isAssignableFrom(ae.getClass()))
            {
                visitField((Field) ae, cache, node, currentLevel, filter);
            }
            else if (Package.class.isAssignableFrom(ae.getClass()))
            {
                visitPackage((Package) ae, cache, node, currentLevel, filter);
            }
            else if (Class.class.isAssignableFrom(ae.getClass()) && ae.getClass().isAnnotation())
            {
                visitClass((Constructor) ae, cache, node, currentLevel, filter);
            }

            else
            {
                // visitObject(object, cache, node, currentLevel,filter);
                throw new IllegalStateException("Can not visist " + ae);
            }
        }
    }

    private <T> void visitConstructor(Constructor<T> ae, Set<Object> cache, ChainedNode node, int currentLevel, Filter filter, Metadata metadata)
    {
        // Params
        // Annotations
        // Exceptions
        Class<? extends Object> currentClass = ae.getClass();

        ChainedNode current = node;

        Class<?>[] family = getAllInterfacesAndClasses(currentClass);
        for (Class<?> elementClass : family)
        { // We iterate over all the family
          // tree of the current class
          //
            if (elementClass != null && !isJdkMember(elementClass))
            {

                for (Constructor<?> c : elementClass.getDeclaredConstructors())
                {
                    if (!isJdkMember(c) && !c.isSynthetic())
                    {
                        current = current.append(ae, c, currentLevel, metadata);
                    }
                }
                //
                for (Method m : elementClass.getDeclaredMethods())
                {
                    if (!isJdkMember(m) && !m.isSynthetic())
                    {
                        current = current.append(ae, m, currentLevel, metadata);
                    }
                }

                for (Field f : elementClass.getDeclaredFields())
                {
                    if (!isJdkMember(f) && !f.isSynthetic())
                    {

                        current = current.append(ae, f, currentLevel, metadata);

                        if (filter != null && filter.retains(f))
                        {
                            Object deeperObject = readField(f, ae);

                            recursiveVisit(deeperObject, cache, current, filter);
                            current = current.last();
                        }
                    }
                }
            }

        }
    }

}
