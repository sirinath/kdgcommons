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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import net.sf.kdgcommons.util.SimpleCLIParser.OptionDefinition;


public class TestSimpleCLIParser extends TestCase
{

    public final static Integer OPT1            = Integer.valueOf(1);
    public final static String  OPT1_ENABLE     = "--enableOne";
    public final static String  OPT1_DISABLE    = "--disableOne";
    public final static int     OPT1_NUMPARAM   = 1;
    public final static String  OPT1_DESC       = "option 1 description";

    public final static Integer OPT2            = Integer.valueOf(2);
    public final static String  OPT2_ENABLE     = "--enableTwo";
    public final static String  OPT2_DISABLE    = "--disableTwo";
    public final static int     OPT2_NUMPARAM   = 0;
    public final static String  OPT2_DESC       = "option 2 description";


//----------------------------------------------------------------------------
//  Support code
//----------------------------------------------------------------------------

    private static class NoOptionParser
    extends SimpleCLIParser
    {
        public NoOptionParser(String... argv)
        {
            super(argv);
        }
    }


    private static class EnableDisableParser
    extends SimpleCLIParser
    {

        public EnableDisableParser(String... argv)
        {
            super(argv,
                  new OptionDefinition(OPT1, OPT1_ENABLE, OPT1_DISABLE, true,  OPT1_DESC),
                  new OptionDefinition(OPT2, OPT2_ENABLE, OPT2_DISABLE, false, OPT2_DESC));
        }
    }


    private static class ParamParser
    extends SimpleCLIParser
    {
        public ParamParser(String... argv)
        {
            super(argv,
                  new OptionDefinition(OPT1, OPT1_ENABLE, OPT1_NUMPARAM, OPT1_DESC),
                  new OptionDefinition(OPT2, OPT2_ENABLE, OPT2_NUMPARAM, OPT2_DESC));
        }
    }


//----------------------------------------------------------------------------
//  Test cases
//----------------------------------------------------------------------------

    public void testParametersOnly() throws Exception
    {
        SimpleCLIParser parser = new NoOptionParser("foo", "bar", "baz");
        assertEquals(Arrays.asList("foo", "bar", "baz"),
                     parser.getParameters());
    }


    public void testShift() throws Exception
    {
        SimpleCLIParser parser = new NoOptionParser("foo", "bar", "baz");

        assertEquals("shift 1", "foo", parser.shift());
        assertEquals("shift 2", "bar", parser.shift());
        assertEquals("shift 3", "baz", parser.shift());
        assertEquals("shift 4", null,  parser.shift());

        assertEquals("getParameters() still returns full list",
                     Arrays.asList("foo", "bar", "baz"),
                     parser.getParameters());
    }


    public void testDefaults() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser();

        assertTrue("opt1 default true",   parser.isOptionEnabled(OPT1));
        assertFalse("opt2 default false", parser.isOptionEnabled(OPT2));

        assertEquals("getOptions()",    Collections.emptyList(), parser.getOptions());
        assertEquals("getParameters()", Collections.emptyList(), parser.getParameters());
    }


    public void testExplicitEnable() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser(OPT1_ENABLE, OPT2_ENABLE);

        assertTrue("opt1", parser.isOptionEnabled(OPT1));
        assertTrue("opt2", parser.isOptionEnabled(OPT2));

        assertEquals("getOptions()",    Arrays.asList(OPT1, OPT2),
                                        parser.getOptions());
        assertEquals("getParameters()", Collections.emptyList(),
                                        parser.getParameters());
    }


    public void testExplicitDisable() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser(OPT1_DISABLE, OPT2_DISABLE);

        assertFalse("opt1", parser.isOptionEnabled(OPT1));
        assertFalse("opt2", parser.isOptionEnabled(OPT2));

        assertEquals("getOptions()",    Arrays.asList(OPT1, OPT2),
                                        parser.getOptions());
        assertEquals("getParameters()", Collections.emptyList(),
                                        parser.getParameters());
    }


    public void testOptionWithParameters() throws Exception
    {
        // opt2 isn't present, so it's "disabled"
        SimpleCLIParser parser = new ParamParser(OPT1_ENABLE, "foo", "bar");

        assertTrue("opt1", parser.isOptionEnabled(OPT1));
        assertFalse("opt2", parser.isOptionEnabled(OPT2));

        assertEquals("getOptions()",        Arrays.asList(OPT1),
                                            parser.getOptions());
        assertEquals("getParameters()",     Arrays.asList("bar"),
                                            parser.getParameters());
        assertEquals("getOptionParameters()", Arrays.asList("foo"),
                                            parser.getOptionValues(OPT1));
    }


    public void testEmbeddedParameters() throws Exception
    {
        SimpleCLIParser parser1 = new ParamParser(OPT1_ENABLE + "=foo");

        assertEquals("getOptions()",        Arrays.asList(OPT1),
                                            parser1.getOptions());
        assertEquals("getParameters()",     Collections.emptyList(),
                                            parser1.getParameters());
        assertEquals("getOptionParameters()", Arrays.asList("foo"),
                                            parser1.getOptionValues(OPT1));

        // note that we don't pay attention to the defined parameter count

        SimpleCLIParser parser2 = new ParamParser(OPT1_ENABLE + "=foo,bar");

        assertEquals("getOptions()",        Arrays.asList(OPT1),
                                            parser2.getOptions());
        assertEquals("getParameters()",     Collections.emptyList(),
                                            parser2.getParameters());
        assertEquals("getOptionParameters()", Arrays.asList("foo","bar"),
                                            parser2.getOptionValues(OPT1));
    }


    public void testMultipleOptionsWithParameters() throws Exception
    {
        // variant 1: options and parameters specified separately
        SimpleCLIParser parser1 = new ParamParser(OPT1_ENABLE, "foo", OPT1_ENABLE, "bar");

        assertEquals("getOptions()",        Arrays.asList(OPT1),
                                            parser1.getOptions());
        assertEquals("getParameters()",     Collections.emptyList(),
                                            parser1.getParameters());
        assertEquals("getOptionParameters()", Arrays.asList("foo", "bar"),
                                            parser1.getOptionValues(OPT1));

        // variant 2: embedded parameters
        SimpleCLIParser parser2 = new ParamParser(OPT1_ENABLE + "=foo", OPT1_ENABLE + "=bar");

        assertEquals("getOptions()",        Arrays.asList(OPT1),
                                            parser2.getOptions());
        assertEquals("getParameters()",     Collections.emptyList(),
                                            parser2.getParameters());
        assertEquals("getOptionParameters()", Arrays.asList("foo", "bar"),
                                            parser2.getOptionValues(OPT1));
    }


    public void testOptionWithoutParameters() throws Exception
    {
        SimpleCLIParser parser = new ParamParser(OPT2_ENABLE, "foo", "bar");

        assertFalse("opt1", parser.isOptionEnabled(OPT1));
        assertTrue("opt2", parser.isOptionEnabled(OPT2));

        assertEquals("getOptions()",        Arrays.asList(OPT2),
                                            parser.getOptions());
        assertEquals("getParameters()",     Arrays.asList("foo", "bar"),
                                            parser.getParameters());
        assertEquals("getOptionParameters()", Collections.emptyList(),
                                            parser.getOptionValues(OPT2));
    }


    public void testEnableDisableOptionWithValue() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser(OPT1_ENABLE + "=foo");

        assertTrue("option is enabled",      parser.isOptionEnabled(OPT1));
        assertEquals("option values",        Arrays.asList("foo"),
                                             parser.getOptionValues(OPT1));
    }


    public void testGetOptionDefs() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser();
        List<OptionDefinition> defs = parser.getAllDefinitions();

        assertEquals("number of definitions", 2, defs.size());
        assertEquals("def 0 key", OPT1, defs.get(0).getKey());
        assertEquals("def 1 key", OPT2, defs.get(1).getKey());
    }


    public void testGetHelp() throws Exception
    {
        SimpleCLIParser parser1 = new EnableDisableParser();
        String helpText1 = parser1.getHelp();

        // not the world's greatest set of assertions, but usage is really the best
        // way to determine what looks good for help text
        assertTrue("option 1 enable",        helpText1.contains(OPT1_ENABLE + " (default)"));
        assertTrue("option 1 disable",       helpText1.contains(OPT1_DISABLE));
        assertTrue("option 1 description",   helpText1.contains(OPT1_DESC));

        SimpleCLIParser parser2 = new ParamParser();
        String helpText2 = parser2.getHelp();

        assertTrue("option 1 enable",        helpText2.contains(OPT1_ENABLE + "=PARAM1"));
        assertTrue("option 1 description",   helpText2.contains(OPT1_DESC));
        assertTrue("option 2 enable",        helpText2.contains(OPT2_ENABLE));
        assertTrue("option 2 description",   helpText2.contains(OPT2_DESC));
    }


    public void testGetDefinitions() throws Exception
    {
        SimpleCLIParser parser = new EnableDisableParser();

        OptionDefinition def1 = parser.getDefinition(OPT1);
        assertEquals("enableVal", def1.getEnableVal(), OPT1_ENABLE);
    }
}
