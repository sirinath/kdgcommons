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
import java.util.HashMap;


/**
 *  This class maintains data in a tabular form: rows and columns, with a
 *  name for each column. It's useful for returning results from a JDBC
 *  query, or as a model for a <code>JTable</code>.
 *  <p>
 *  Note for JDBC: column indexes in <code>DataTable</code> are numbered
 *  from 0, <em>not</em> 1.
 */

public class DataTable
{
    private String[] _colNames;
    private Class<?>[] _colClasses;
    private ArrayList<Object[]> _data;
    private HashMap<String,Integer> _name2Col;


    /**
     *  Base constructor.
     *
     *  @param  colNames    The names for each column. This defines the width
     *                      of the table; attempting to add rows with more or
     *                      fewer columns causes an exception. Names in this
     *                      array should be unique; if not, you will be unable
     *                      to retrieve column values by name.
     *  @param  colClasses  The class of each column. Attempting to add values
     *                      of the wrong class causes a <code>ClassCastException
     *                      </code>. May be <code>null</code> or contain <code>
     *                      null</code> elements to disable class checking for
     *                      the entire table or particular column respectively.
     *  @param  data        Initial data for the table. May be <code>null</code>
     *                      to create an empty table. If not <code>null</code>,
     *                      this data is checked for number of columns and value
     *                      class.
     */
    public DataTable(
        String[]    colNames,
        Class<?>[]  colClasses,
        Object[][]  data
        )
    {
        if ((colClasses != null) && (colClasses.length != colNames.length))
        {
            throw new IllegalArgumentException(
                "colClasses not same size as colNames"
                + " (expected: " + _colNames.length
                + ", got: " + colClasses.length + ")");
        }

        _colNames = new String[colNames.length];
        _name2Col = new HashMap<String,Integer>();
        _colClasses = new Class[_colNames.length];
        for (int col = 0 ; col < colNames.length ; col++)
        {
            _colNames[col] = colNames[col];
            _name2Col.put(colNames[col], Integer.valueOf(col));
            _colClasses[col] = (colClasses != null) ? colClasses[col] : null;
        }

        _data = new ArrayList<Object[]>();
        if (data != null)
        {
            for (int row = 0 ; row < data.length ; row++)
            {
                internalAddRow(row, data[row]);
            }
        }
    }


    /**
     *  Convenience constructor that just takes column names, creates an
     *  empty table that doesn't check data class.
     */
    public DataTable(String[] colNames)
    {
        this(colNames, null, null);
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Returns the number of rows in this table.
     */
    public int size()
    {
        return _data.size();
    }


    /**
     *  Returns the number of columns in this table.
     */
    public int getColumnCount()
    {
        return _colNames.length;
    }


    /**
     *  Returns the name of a column.
     */
    public String getColumnName(int col)
    {
        return _colNames[col];
    }


    /**
     *  Returns the class of a column, <code>null</code> if the column can
     *  hold values of any class.
     */
    public Class<?> getColumnClass(int col)
    {
        return _colClasses[col];
    }


    /**
     *  Returns the value at an existing row/column location.
     *
     *  @throws IndexOutOfBoundsException if the specified row or column is
     *          not within the bounds of the table.
     */
    public Object getValue(int row, int col)
    {
        Object[] rowData = (Object[])_data.get(row);
        return rowData[col];
    }


    /**
     *  Sets the value at an existing row/column location.
     *
     *  @return The object previously at that location.
     *  @throws ClassCastException if the passed value is an incorrect class
     *          for the column.
     *  @throws IndexOutOfBoundsException if the specified row or column is
     *          not within the bounds of the table.
     */
    public Object setValue(int row, int col, Object val)
    {
        checkClass(row, col, val);
        Object[] rowData = (Object[])_data.get(row);
        Object oldValue = rowData[col];
        rowData[col] = val;
        return oldValue;
    }


    /**
     *  Adds a new empty row to the end of the table.
     */
    public void addRow()
    {
        internalAddRow(_data.size(), new Object[getColumnCount()]);
    }


    /**
     *  Adds a new row to the end of the table, with specified data.
     *
     *  @throws IllegalArgumentException if the passed row is an incorrect
     *          size for the table.
     *  @throws ClassCastException if an element within the row does not have
     *          the correct class for its column.
     */
    public void addRow(Object[] rowData)
    {
        internalAddRow(_data.size(), rowData);
    }


//----------------------------------------------------------------------------
//  Internal methods
//----------------------------------------------------------------------------

    /**
     *  Verifies that a passed row is the correct size for this table.
     *
     *  @throws IllegalArgumentException if it is not.
     */
    private void checkRowSize(int row, Object[] rowData)
    {
        if (rowData.length != _colNames.length)
        {
            throw new IllegalArgumentException(
                "row[" + row + "] has incorrect size: "
                + "expected: " + _colNames.length
                + ", got: " + rowData.length);
        }
    }


    /**
     *  Verifies that an object is the correct class for a column, throwing
     *
     *  @throws ClassCastException if the object does not have the correct
     *          class.
     */
    private void checkClass(int row, int col, Object obj)
    {
        if ((_colClasses[col] != null)
            && (obj != null)
            && !_colClasses[col].isInstance(obj))
        {
            throw new ClassCastException(
                "cell[" + row + "," + col + "]: "
                + "expected " + _colClasses[col].getName()
                + ", got " + obj.getClass().getName());
        }
    }


    /**
     *  A non-overridable way to add rows; common to constructor and {@link
     *  addRow}.
     */
    private void internalAddRow(int row, Object[] rowData)
    {
        checkRowSize(row, rowData);
        Object[] lclData = new Object[_colNames.length];
        for (int col = 0 ; col < _colNames.length ; col++)
        {
            checkClass(row, col, rowData[col]);
            lclData[col] = rowData[col];
        }
        _data.add(lclData);
    }
}
