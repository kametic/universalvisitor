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
package org.nuunframework.universalvisitor.api;

import java.lang.reflect.AnnotatedElement;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Node;
import org.kametic.universalvisitor.core.MapReduceDefault;

public class MapReduceTest
{

    @SuppressWarnings("unchecked")
    @Test
    public void test()
    {
        Assertions.assertThat(new MapReduceDefault<String>(new MyMapper()).getReducers()).isNotNull();
        Assertions.assertThat(new MapReduceDefault<String>(new MyMapper()).getReducers()).isEmpty();
    }

    static class MyMapper implements Mapper<String>
    {

        @Override
        public boolean handle(AnnotatedElement object)
        {
            return false;
        }

        @Override
        public String map(Node node)
        {
            return null;
        }
    }

}
