// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sf.kdgcommons.testinternals;


/**
 *  This class is used to test reflection utilities: if you hold a <code>Method</code>
 *  retrieved from <code>InaccessibleClassImpl</code> you will not be able to invoke
 *  that method without calling <code>setAccessible()</code>.
 *  <p>
 *  This class lives in an isolated package to ensure that we don't have unexpected
 *  accessiblity. Don't put any test code in here.
 */
public abstract class InaccessibleClass
{
    public static InaccessibleClass newInstance()
    {
        return new InaccessibleClassImpl();
    }
    
    public abstract String getValue();
    public abstract void setValue(String value);
    

    static class InaccessibleClassImpl
    extends InaccessibleClass
    {
        private String _value;
        
        @Override
        public String getValue()            { return _value; }
        
        @Override
        public void setValue(String value)  { _value = value; }
    }
}
