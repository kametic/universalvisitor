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
package org.kametic.universalvisitor.api;

/**
 * 
 * Metadata is the object held by the Node that keeps metadata like 
 * <ul>
 *   <li> if needed the index, if the element is inside an array or alist.
 *   <li> if needed the key, if the element is inside a Map.
 * </ul>
 * 
 * @author epo.jemba@kametic.com
 *
 */
public class Metadata
{

    private int    index = -1;
    private Object key   = null;

    /**
     * Default constructor
     */
    public Metadata()
    {
    }

    /**
     * Constructor with a key as parameter.
     * 
     * @param key
     */
    public Metadata(Object key)
    {
        this.key = key;
    }

    /**
     * Constructor with an index as parameter.
     * 
     * @param index
     */
    public Metadata(int index)
    {
        this.index = index;
    }

    public int index()
    {
        return index;
    }

    public Object key()
    {
        return key;
    }

    @Override
    public String toString()
    {
        String metadata = "";

        if (index > -1)
        {
            metadata = "[" + index + "]";
        }

        if (key != null)
        {
            metadata += "[" + key + "]";
        }

        return metadata;
    }
}