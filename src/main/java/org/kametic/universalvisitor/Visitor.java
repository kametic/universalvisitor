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

import org.kametic.universalvisitor.api.Filter;
import org.kametic.universalvisitor.api.Job;
import org.kametic.universalvisitor.api.MapReduce;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Reducer;

public interface Visitor<F extends Filter<?>>
{

    public <T> void visit(Object o, Mapper<T> mapper);

    public <T> void visit(Object o, F filter, Mapper<T> mapper);

    public <T> void visit(Object o, Mapper<T> mapper, Reducer<T, ?>... reducers);

    public <T> void visit(Object o, F filter, Mapper<T> mapper, Reducer<T, ?>... reducers);

    public <T> void visit(Object o, MapReduce<T>... mapReduces);

    public <T> void visit(Object o, F filter, MapReduce<T>... mapReduces);

    public void visit(Object o, Job<?> job);

    public void visit(Object o, F filter, Job<?> job);

}