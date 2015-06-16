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
 * A node is the element type of the internal linked list created during the visit of the object graph.
 * <p>
 * It has:
 * </p>
 * <ul>
 *   <li> the visited annotated element : Field, Method, Constructor, Class or Package.</li>
 *   <li> the Metadata associated with the node.</li>
 *   <li> the level inside the graph visited it starts with 0.</li>
 *   <li> the instance of the visited annotated element.</li>
 * </ul>
 * 
 * @author epo.jemba@kametic.com
 */
public interface Node
{

    AnnotatedElement annotatedElement();

    Metadata metadata();

    int level();

    Object instance();

}
