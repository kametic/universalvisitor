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
import org.kametic.universalvisitor.api.Node;

/**
 * @author Epo Jemba
 * @author Pierre Thirouin
 */
public class NodeDefault implements Node
{

    private Object           instance;
    private Metadata         metadata = null;
    private AnnotatedElement annotatedElement;
    protected int            level    = 0;

    protected NodeDefault(Object instance, AnnotatedElement annotatedElement)
    {
        super();
        this.instance = instance;
        this.annotatedElement = annotatedElement;
    }

    protected NodeDefault(Object instance, AnnotatedElement annotatedElement, int level)
    {
        this(instance, annotatedElement);
        this.level = level;
    }

    protected NodeDefault(Object instance, AnnotatedElement annotatedElement, int level, Metadata metadata)
    {
        this(instance, annotatedElement);
        this.level = level;
        this.metadata = metadata;
    }

    protected NodeDefault(Object instance, AnnotatedElement annotatedElement, int level, int index)
    {
        this(instance, annotatedElement);
        this.level = level;
        metadata = new Metadata(index);
    }

    protected NodeDefault(Object instance, AnnotatedElement annotatedElement, int level, Object key)
    {
        this(instance, annotatedElement);
        this.level = level;
        metadata = new Metadata(key);
    }

    @Override
    public Object instance()
    {
        return instance;
    }

    @Override
    public int level()
    {
        return level;
    }

    @Override
    public Metadata metadata()
    {
        return metadata;
    }

    @Override
    public AnnotatedElement visitedElement()
    {
        return annotatedElement;
    }

    public Metadata metadata(int index)
    {
        if (metadata == null)
        {
            metadata = new Metadata(index);
        }

        return metadata;
    }

    public Metadata metadata(Object key)
    {
        if (metadata == null)
        {
            metadata = new Metadata(key);
        }
        return metadata;
    }

    public Node metadata(Metadata metadata)
    {
        this.metadata = metadata;
        return this;
    }

    public void annotatedElement(AnnotatedElement annotatedElement)
    {
        this.annotatedElement = annotatedElement;
    }

    @Override
    public String toString()
    {
        return "Node [instance=" + instance + ", annotatedElement=" + annotatedElement + ", level=" + level + "]";
    }

}