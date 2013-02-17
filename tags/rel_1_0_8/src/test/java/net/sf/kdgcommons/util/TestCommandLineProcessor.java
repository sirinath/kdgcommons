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

package net.sf.kdgcommons.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.TestCase;


public class TestCommandLineProcessor extends TestCase
{
//----------------------------------------------------------------------------
//  Support Classes
//----------------------------------------------------------------------------

    /**
     *  A processor that keeps track of its unprocessed arguments, and allows
     *  addition of external handlers.
     */
    private static class SimpleProcessor
    extends CommandLineProcessor
    {
        public String[] unprocessedArgs;

        @Override
        public void addHandler(OptionHandler handler)
        {
            super.addHandler(handler);
        }

        @Override
        protected void handleUnprocessedArguments(List<String> args)
        {
            unprocessedArgs = args.toArray(new String[args.size()]);
        }
    }


    /**
     *  A generic handler that takes a single argument, specified in
     *  its constructor. Maintains a list of all arguments processed,
     *  so it can be invoked more than once.
     */
    private static class SimpleHandler
    implements CommandLineProcessor.OptionHandler
    {
        private String _optionName;
        public List<String> values = new ArrayList<String>();

        public SimpleHandler(String name)
        {
            _optionName = name;
        }

        public String getArgumentDescription()
        {
            return null;
        }

        public String getOptionDescription()
        {
            return null;
        }

        public String getOptionName()
        {
            return _optionName;
        }

        public void process(ListIterator<String> args)
        {
            values.add(args.next());
        }
    }


//----------------------------------------------------------------------------
//  Test Code
//----------------------------------------------------------------------------

    public void testTestWithNoHandlers() throws Exception
    {
        SimpleProcessor proc = new SimpleProcessor();

        proc.process(new String[] {"this", "is", "a", "test"});
        assertEquals(4, proc.unprocessedArgs.length);
        assertEquals("this", proc.unprocessedArgs[0]);
        assertEquals("is",   proc.unprocessedArgs[1]);
        assertEquals("a",    proc.unprocessedArgs[2]);
        assertEquals("test", proc.unprocessedArgs[3]);
    }


    public void testTestWithSingleHandler() throws Exception
    {
        SimpleHandler handler = new SimpleHandler("--test");
        SimpleProcessor proc = new SimpleProcessor();
        proc.addHandler(handler);

        proc.process(new String[] {"this", "--test", "is", "a"});
        assertEquals(2, proc.unprocessedArgs.length);
        assertEquals("this", proc.unprocessedArgs[0]);
        assertEquals("a",    proc.unprocessedArgs[1]);

        assertEquals(1, handler.values.size());
        assertEquals("is", handler.values.get(0));
    }
}
