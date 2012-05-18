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

import junit.framework.*;


public class TestDataTable extends TestCase
{
    public TestDataTable(java.lang.String testName)
    {
        super(testName);
    }


//------------------------------------------------------------------------------
//  Setup
//------------------------------------------------------------------------------

    private final static String[] HEADERS = new String[]
    {
        "Argle", "Bargle", "Wargle"
    };

    private final static Class<?>[] CLASSES = new Class<?>[]
    {
        String.class, String.class, String.class
    };

    private final static Object[][] GOOD_DATA = new Object[][]
    {
        new Object[] { "A", "B", "C" },
        new Object[] { "D", "E", "F" },
        new Object[] { "G", "H", "I" },
        new Object[] { "J", "K", "L" }
    };

    private final static Object[][] BAD_DATA = new Object[][]
    {
        new Object[] { "A", "B", new Integer(123) },
        new Object[] { "D", "E", "F" }
    };


//------------------------------------------------------------------------------
//  Support code
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
//  Test Methods
//------------------------------------------------------------------------------

    public void testBasicConstructor() throws Exception
    {
        DataTable table = new DataTable(HEADERS, CLASSES, GOOD_DATA);
        assertEquals("width", HEADERS.length, table.getColumnCount());
        assertEquals("height", GOOD_DATA.length, table.size());
        for (int col = 0 ; col < HEADERS.length ; col++)
        {
            assertEquals("column name", HEADERS[col], table.getColumnName(col));
            assertEquals("column class", CLASSES[col], table.getColumnClass(col));
            for (int row = 0 ; row < GOOD_DATA.length ; row++)
            {
                assertEquals("value[" + row + "," + col + "]",
                             GOOD_DATA[row][col], table.getValue(row, col));
            }
        }
    }


    public void testConstructionWithInvalidData() throws Exception
    {
        try
        {
            new DataTable(HEADERS, CLASSES, BAD_DATA);
            fail("did not catch invalid data");
        }
        catch (ClassCastException e)
        {
            // success
        }
    }


    public void testConvenienceCtor() throws Exception
    {
        DataTable table = new DataTable(new String[] {"foo", "bar", "baz"});
        assertEquals(0, table.size());
        assertEquals(3, table.getColumnCount());
        assertNull(table.getColumnClass(0));
        assertNull(table.getColumnClass(1));
        assertNull(table.getColumnClass(2));
    }


    public void testAddRow() throws Exception
    {
        DataTable table = new DataTable(new String[] {"h1", "h2", "h3"});

        table.addRow(new Object[] {"foo", "bar", "baz"});
        assertEquals(1, table.size());
        assertEquals("foo", table.getValue(0, 0));
        assertEquals("bar", table.getValue(0, 1));
        assertEquals("baz", table.getValue(0, 2));

        table.addRow();
        assertEquals(2, table.size());
        assertNull(table.getValue(1, 0));
        assertNull(table.getValue(1, 1));
        assertNull(table.getValue(1, 1));
    }


    public void testSetValue() throws Exception
    {
        DataTable table = new DataTable(new String[] {"h1", "h2", "h3"});
        table.addRow(new Object[] {"foo", "bar", "baz"});
        table.addRow(new Object[] {"argle", "wargle", "bargle"});

        table.setValue(0, 1, "bargle");
        table.setValue(1, 1, "bar");

        assertEquals("foo", table.getValue(0, 0));
        assertEquals("bargle", table.getValue(0, 1));
        assertEquals("baz", table.getValue(0, 2));

        assertEquals("argle", table.getValue(1, 0));
        assertEquals("bar", table.getValue(1, 1));
        assertEquals("bargle", table.getValue(1, 2));
    }

}
