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

import org.kametic.universalvisitor.api.MapReduce;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Reducer;

public class MapReduceDefault<T> implements MapReduce<T>
{

    private Mapper<T>       mapper;
    private Reducer<T, ?>[] reducers;

    public MapReduceDefault(Mapper<T> mapper, Reducer<T, ?>... reducers)
    {
        this.mapper = mapper;
        this.reducers = reducers;
    }

    public Mapper<T> getMapper()
    {
        return mapper;
    }

    public Reducer<T, ?>[] getReducers()
    {
        return reducers;
    }

    @Override
    public Object aggregate()
    {
        return null;
    }

}
