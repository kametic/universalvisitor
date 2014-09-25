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
package org.kametic.universalvisitor.core;

import java.lang.reflect.AnnotatedElement;

import org.kametic.universalvisitor.api.Metadata;

/**
 * @author epo.jemba@kametic.com
 */
public class ChainedNode extends NodeDefault
{
    private ChainedNode next;

    protected ChainedNode(Object instance, AnnotatedElement annotatedElement, int level, ChainedNode next)
    {
        super(instance, annotatedElement, level);
        this.next = next;
    }

    private void next(ChainedNode node)
    {
        if (next != null)
        {
            throw new IllegalStateException("next pair can not be set twice.");
        }
        next = node;
    }

    public static ChainedNode createRoot()
    {
        return new ChainedNode(new Object(), null, -1, null);
    }

    public ChainedNode append(Object o, AnnotatedElement ao, int level, Metadata metadata)
    {

        next(new ChainedNode(o, ao, level, null).metadata(metadata));

        return next;
    }

    @Override
    public ChainedNode metadata(Metadata metadata)
    {
        return (ChainedNode) super.metadata(metadata);
    }

    public ChainedNode next()
    {
        return next;
    }

    public ChainedNode last()
    {
        if (next != null)
        {
            return next.last();
        }
        else
        {
            return this;
        }
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
                "%sChainedNode [ %s@%s , level=%s , annotatedElement=%s] \n%s",
                indentation,
                instance().getClass().getSimpleName(),
                Integer.toHexString(instance().hashCode()),
                level(),
                visitedElement(),
                next);
        return rep;

        // return "ChainedNode [instance()=" + instance() + ", level()="
        // + level() + ", annotatedElement()=" + annotatedElement()
        // + "]  ==> \n" + next;
    }

}