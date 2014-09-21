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
package org.nuunframework.universalvisitor.sample.issues;

/**
 * @author epo.jemba@kametic.com
 */
public class Issue2 extends Parent
{

    public Issue1    issue2Public    = new Issue1();
    protected Issue1 issue2Protected = new Issue1();
    Issue1           issue2Package   = new Issue1();
    private Issue1   issue2Private   = new Issue1();

    private void issue2Public()
    {
    }

    protected void issue2Protected()
    {
    }

    void issue2Package()
    {
    }

    public void issue2Private()
    {
    }

    @Override
    public void interface1M()
    {
    }

    @Override
    public int interface2M()
    {
        return 0;
    }

    @Override
    public long interface3M()
    {
        return 0;
    }

}
