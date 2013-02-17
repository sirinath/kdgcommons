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

package net.sf.kdgcommons.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *  A collection of utility methods for working at the JDBC level.
 */
public class JDBCUtil
{
    /**
     *  Closes the passed <code>Connection</code> ignoring exceptions. This is usually
     *  called in a <code>finally</code> block, and throwing an exception there would
     *  overwrite any exception thrown by the main code (and if there is an exception
     *  when closing, there probably was one there as well).
     */
    public static void closeQuietly(Connection cxt)
    {
        if (cxt != null)
        {
            try
            {
                cxt.close();
            }
            catch (SQLException ignored)
            { /* nothing here */ }
        }
    }


    /**
     *  Closes the passed <code>ResultSet</code> ignoring exceptions. This is usually
     *  called in a <code>finally</code> block, and throwing an exception there would
     *  overwrite any exception thrown by the main code (and if there is an exception
     *  when closing, there probably was one there as well).
     */
    public static void closeQuietly(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException ignored)
            { /* nothing here */ }
        }
    }


    /**
     *  Closes the passed <code>ResultSet</code> ignoring exceptions. This is usually
     *  called in a <code>finally</code> block, and throwing an exception there would
     *  overwrite any exception thrown by the main code (and if there is an exception
     *  when closing, there probably was one there as well).
     */
    public static void closeQuietly(ResultSet rslt)
    {
        if (rslt != null)
        {
            try
            {
                rslt.close();
            }
            catch (SQLException ignored)
            { /* nothing here */ }
        }
    }
}
