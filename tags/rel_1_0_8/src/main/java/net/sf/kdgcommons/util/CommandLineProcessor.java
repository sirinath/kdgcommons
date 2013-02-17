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
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;


/**
 *  A base class for handling invocation arguments, including automated
 *  generation of a "usage" message. Subclasses define handlers for each
 *  of the options they process, and add those handlers to a handler map.
 *  Handlers are responsible for consuming arguments and storing them as
 *  appropriate.
 *  <p>
 *  The program's <code>main()</code> will instantiate the subclass and
 *  then call {@link #process}.
 *  <p>
 *  At the present time, this class does not support combined arguments.
 *  However, since they will be passed to the subclass' default method,
 *  it can handle them if needed.
 *  
 *  @Deprecated {@link SimpleCLIParser} is my current preferred implementation.
 *              It's a lot simpler: there's no need for handler objects,
 *              and the options are processed by the constructor. However,
 *              I still have code that depends on this class, so it will
 *              stick around until the (never expected) 2.0 release.
 */
public abstract class CommandLineProcessor
{
    private TreeMap<String,OptionHandler> _handlers
            = new TreeMap<String,OptionHandler>();

//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Processes the command-line options. This will iterator through the
     *  passed option array, and call handlers for each recognized option.
     *  After all recognized options have been processed, it will call the
     *  method {@link #handleUnprocessedArguments}.
     */
    public void process(String[] argv)
    {
        List<String> unprocessed = new ArrayList<String>();
        ListIterator<String> itx = Arrays.asList(argv).listIterator();
        while (itx.hasNext())
        {
            String arg = itx.next();
            OptionHandler handler = _handlers.get(arg);
            if (handler != null)
                handler.process(itx);
            else
                unprocessed.add(arg);
        }
        handleUnprocessedArguments(unprocessed);
    }

//----------------------------------------------------------------------------
//  Internals and stuff for subclasses
//----------------------------------------------------------------------------

    /**
     *  Adds an option handler to the list. This is typically called by the
     *  subclass constructor.
     */
    protected void addHandler(OptionHandler handler)
    {
        _handlers.put(handler.getOptionName(), handler);
    }


    /**
     *  Subclass-defined argument handlers must implement this interface.
     */
    protected interface OptionHandler
    {
        /**
         *  Returns the name of the option. Since this is used to match the
         *  command-line argument, it must include any leading dashes. For
         *  example: "--input".
         */
        public String getOptionName();


        /**
         *  Returns a description of this option, for inclusion in the usage
         *  output. Example: "the file to be processed".
         */
        public String getOptionDescription();

        /**
         *  Returns text describing the option's arguments, for inclusion
         *  in the usage output. For example, "FILENAME [FORMAT]".
         */
        public String getArgumentDescription();

        /**
         *  Processes the option. This is called after matching a command-
         *  line argument to this handler, and the passed iterator points
         *  points at the next argument on the command line.
         */
        public void process(ListIterator<String> args);
    }


    /**
     *  This method is called after all options have been handled. It allows
     *  the subclass to process arguments that are not specified as belonging
     *  to a particular option.
     *  <p>
     *  Note: the "unprocessed" arguments may appear anywhere in the command
     *  line. It is essentially the full list of arguments, minus any that
     *  represent options or their consumed arguments.
     *  <p>
     *  Default behavior is to ignore unprocessed arguments. This exists
     *  primarily to simplify test classes, although I suppose some weird
     *  program might not have any command-line arguments that aren't tied
     *  to an option.
     */
    protected void handleUnprocessedArguments(List<String> args)
    {
        // nothing happening here
    }
}
