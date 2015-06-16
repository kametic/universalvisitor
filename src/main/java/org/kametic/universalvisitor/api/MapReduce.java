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
 * A MapReduce of T represents :
 * <ul>
 *    <li> one Mapper of  &lt;T&gt; </li>
 *    <li> one ore more Reducer of &lt;T,?&gt; </li>
 * </ul>
 * It has a method aggregate() whose purpose is to aggregate all the reduction into one Aggregation.
 * <p>
 * MapReduceDefault is the implementation we supply.
 * </p>
 *
 * @author epo.jemba
 *
 * @param <T> the object type to map/reduce
 */
public interface MapReduce<T>
{

    public Mapper<T> getMapper();

    public Reducer<T, ?>[] getReducers();

    public Object aggregate();

}
