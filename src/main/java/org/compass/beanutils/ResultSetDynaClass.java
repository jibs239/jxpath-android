/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.compass.beanutils;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;


/**
 * <p>Implementation of <code>DynaClass</code> for DynaBeans that wrap the
 * <code>java.sql.Row</code> objects of a <code>java.sql.ResultSet</code>.
 * The normal usage pattern is something like:</p>
 * <pre>
 *   ResultSet rs = ...;
 *   ResultSetDynaClass rsdc = new ResultSetDynaClass(rs);
 *   Iterator rows = rsdc.iterator();
 *   while (rows.hasNext())  {
 *     DynaBean row = (DynaBean) rows.next();
 *     ... process this row ...
 *   }
 *   rs.close();
 * </pre>
 *
 * <p>Each column in the result set will be represented as a DynaBean
 * property of the corresponding name (optionally forced to lower case
 * for portability).</p>
 *
 * <p><strong>WARNING</strong> - Any {@link DynaBean} instance returned by
 * this class, or from the <code>Iterator</code> returned by the
 * <code>iterator()</code> method, is directly linked to the row that the
 * underlying result set is currently positioned at.  This has the following
 * implications:</p>
 * <ul>
 * <li>Once you retrieve a different {@link DynaBean} instance, you should
 *     no longer use any previous instance.</li>
 * <li>Changing the position of the underlying result set will change the
 *     data that the {@link DynaBean} references.</li>
 * <li>Once the underlying result set is closed, the {@link DynaBean}
 *     instance may no longer be used.</li>
 * </ul>
 *
 * <p>Any database data that you wish to utilize outside the context of the
 * current row of an open result set must be copied.  For example, you could
 * use the following code to create standalone copies of the information in
 * a result set:</p>
 * <pre>
 *   ArrayList results = new ArrayList(); // To hold copied list
 *   ResultSetDynaClass rsdc = ...;
 *   DynaProperty[] properties = rsdc.getDynaProperties();
 *   BasicDynaClass bdc =
 *     new BasicDynaClass("foo", BasicDynaBean.class,
 *                        rsdc.getDynaProperties());
 *   Iterator rows = rsdc.iterator();
 *   while (rows.hasNext()) {
 *     DynaBean oldRow = (DynaBean) rows.next();
 *     DynaBean newRow = bdc.newInstance();
 *     PropertyUtils.copyProperties(newRow, oldRow);
 *     results.add(newRow);
 *   }
 * </pre>
 *
 * @version $Id: ResultSetDynaClass.java 1540186 2013-11-08 21:08:30Z oheger $
 */

public class ResultSetDynaClass extends JDBCDynaClass implements DynaClass {


    // ----------------------------------------------------------- Constructors


    /**
     * <p>The <code>ResultSet</code> we are wrapping.</p>
     */
    protected ResultSet resultSet = null;


    /**
     * <p>Construct a new ResultSetDynaClass for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to column names in the result set will be lower cased.</p>
     *
     * @param resultSet The result set to be wrapped
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public ResultSetDynaClass(ResultSet resultSet) throws SQLException {

        this(resultSet, true);

    }


    /**
     * <p>Construct a new ResultSetDynaClass for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to the column names in the result set will be lower cased or not,
     * depending on the specified <code>lowerCase</code> value.</p>
     *
     * <p><strong>WARNING</strong> - If you specify <code>false</code>
     * for <code>lowerCase</code>, the returned property names will
     * exactly match the column names returned by your JDBC driver.
     * Because different drivers might return column names in different
     * cases, the property names seen by your application will vary
     * depending on which JDBC driver you are using.</p>
     *
     * @param resultSet The result set to be wrapped
     * @param lowerCase Should property names be lower cased?
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public ResultSetDynaClass(ResultSet resultSet, boolean lowerCase)
            throws SQLException {

        this(resultSet, lowerCase, false);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * <p>Construct a new ResultSetDynaClass for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to the column names in the result set will be lower cased or not,
     * depending on the specified <code>lowerCase</code> value.</p>
     *
     * <p><strong>WARNING</strong> - If you specify <code>false</code>
     * for <code>lowerCase</code>, the returned property names will
     * exactly match the column names returned by your JDBC driver.
     * Because different drivers might return column names in different
     * cases, the property names seen by your application will vary
     * depending on which JDBC driver you are using.</p>
     *
     * @param resultSet      The result set to be wrapped
     * @param lowerCase      Should property names be lower cased?
     * @param useColumnLabel true if the column label should be used, otherwise false
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     * @since 1.8.3
     */
    public ResultSetDynaClass(ResultSet resultSet, boolean lowerCase, boolean useColumnLabel)
            throws SQLException {

        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.resultSet = resultSet;
        this.lowerCase = lowerCase;
        setUseColumnLabel(useColumnLabel);
        introspect(resultSet);

    }


    // --------------------------------------------------------- Public Methods

    /**
     * <p>Return an <code>Iterator</code> of {@link DynaBean} instances for
     * each row of the wrapped <code>ResultSet</code>, in "forward" order.
     * Unless the underlying result set supports scrolling, this method
     * should be called only once.</p>
     *
     * @return An <code>Iterator</code> of {@link DynaBean} instances
     */
    public Iterator<DynaBean> iterator() {

        return (new ResultSetIterator(this));

    }


    /**
     * Get a value from the {@link ResultSet} for the specified
     * property name.
     *
     * @param name The property name
     * @return The value
     * @throws SQLException if an error occurs
     * @since 1.8.0
     */
    public Object getObjectFromResultSet(String name) throws SQLException {
        return getObject(getResultSet(), name);
    }

    // -------------------------------------------------------- Package Methods


    /**
     * <p>Return the result set we are wrapping.</p>
     */
    ResultSet getResultSet() {

        return (this.resultSet);

    }


    // ------------------------------------------------------ Protected Methods

    /**
     * <p>Loads the class of the given name which by default uses the class loader used
     * to load this library.
     * Dervations of this class could implement alternative class loading policies such as
     * using custom ClassLoader or using the Threads's context class loader etc.
     * </p>
     *
     * @param className The name of the class to load
     * @return The loaded class
     * @throws SQLException if the class cannot be loaded
     */
    @Override
    protected Class<?> loadClass(String className) throws SQLException {

        try {
            return getClass().getClassLoader().loadClass(className);
        } catch (Exception e) {
            throw new SQLException("Cannot load column class '" +
                    className + "': " + e);
        }
    }
}
