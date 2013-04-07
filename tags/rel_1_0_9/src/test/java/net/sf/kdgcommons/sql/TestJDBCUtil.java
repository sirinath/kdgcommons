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

import junit.framework.TestCase;

import net.sf.kdgcommons.test.ExceptionMock;
import net.sf.kdgcommons.test.SimpleMock;


/**
 *  Tests for the JDBC utilities. These tests use internally-defined faux
 *  objects rather than relying on a real database (such as Hypersonic).
 */
public class TestJDBCUtil extends TestCase
{

    public TestJDBCUtil(String testName)
    {
        super(testName);
    }


//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------


//----------------------------------------------------------------------------
//  Test cases
//----------------------------------------------------------------------------

    public void testCloseQuietly()
    throws Exception
    {
        SimpleMock cxtMock = new SimpleMock();
        Connection cxt = cxtMock.getInstance(Connection.class);
        JDBCUtil.closeQuietly(cxt);
        cxtMock.assertCallCount(1);
        cxtMock.assertCall(0, "close");

        SimpleMock stmtMock = new SimpleMock();
        Statement stmt = stmtMock.getInstance(Statement.class);
        JDBCUtil.closeQuietly(stmt);
        stmtMock.assertCallCount(1);
        stmtMock.assertCall(0, "close");

        SimpleMock rsltMock = new SimpleMock();
        ResultSet rslt = rsltMock.getInstance(ResultSet.class);
        JDBCUtil.closeQuietly(rslt);
        rsltMock.assertCallCount(1);
        rsltMock.assertCall(0, "close");
    }


    public void testCloseQuietlyWhenNull()
    throws Exception
    {
        // compiler needs us to specify the type of the argument to pick the
        // correct method ... we could just cast a null, but creating vars
        // lets us follow the same code pattern as the other two tests

        Connection cxt = null;
        JDBCUtil.closeQuietly(cxt);

        Statement stmt = null;
        JDBCUtil.closeQuietly(stmt);

        ResultSet rslt = null;
        JDBCUtil.closeQuietly(rslt);
    }


    public void testCloseQuietlyWithException()
    throws Exception
    {
        ExceptionMock mock = new ExceptionMock(SQLException.class);

        Connection cxt = mock.getInstance(Connection.class);
        JDBCUtil.closeQuietly(cxt);

        Statement stmt = mock.getInstance(Statement.class);
        JDBCUtil.closeQuietly(stmt);

        ResultSet rslt = mock.getInstance(ResultSet.class);
        JDBCUtil.closeQuietly(rslt);
    }
}
