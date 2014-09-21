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

import java.lang.reflect.AnnotatedElement;

/**
 * A Mapper will "map" all the AnnotatedElement of the visited object graph, it will allows.
 * <p>
 * All results of the map(Node) method will be then given to one or more Reducers.  
 * 
 * @author Epo Jemba
 * @author Pierre Thirouin
 */
public interface Mapper<O>
{

    /**
     * this method will tell the UniversalVisitor if this mapper may handle object, the AnnotatedElement.
     * 
     * @param object the AnnotatedElement given in parameter.
     * @return true if this mapper have to handle the AnnotatedElement, false if not.
     */
    boolean handle(AnnotatedElement object);

    /**
     * The implementation of the actual mapping given the node in parameter.
     * 
     * @param node
     * @return the actual result of the Map.
     */
    O map(Node node);

}
