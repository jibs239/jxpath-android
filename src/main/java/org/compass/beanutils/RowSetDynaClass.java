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


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Implementation of {@link DynaClass} that creates an in-memory collection
 * of {@link DynaBean}s representing the results of an SQL query.  Once the
 * {@link DynaClass} instance has been created, the JDBC <code>ResultSet</code>
 * and <code>Statement</code> on which it is based can be closed, and the
 * underlying <code>Connection</code> can be returned to its connection pool
 * (if you are using one).</p>
 *
 * <p>The normal usage pattern is something like:</p>
 * <pre>
 *   Connection conn = ...;  // Acquire connection from pool
 *   Statement stmt = conn.createStatement();
 *   ResultSet rs = stmt.executeQuery("SELECT ...");
 *   RowSetDynaClass rsdc = new RowSetDynaClass(rs);
 *   rs.close();
 *   stmt.close();
 *   ...;                    // Return connection to pool
 *   List rows = rsdc.getRows();
 *   ...;                   // Process the rows as desired
 * </pre>
 *
 * <p>Each column in the result set will be represented as a {@link DynaBean}
 * property of the corresponding name (optionally forced to lower case
 * for portability).  There will be one {@link DynaBean} in the
 * <code>List</code> returned by <code>getRows()</code> for each
 * row in the original <code>ResultSet</code>.</p>
 *
 * <p>In general, instances of {@link RowSetDynaClass} can be serialized
 * and deserialized, which will automatically include the list of
 * {@link DynaBean}s representing the data content.  The only exception
 * to this rule would be when the underlying property values that were
 * copied from the <code>ResultSet</code> originally cannot themselves
 * be serialized.  Therefore, a {@link RowSetDynaClass} makes a very
 * convenient mechanism for transporting data sets to remote Java-based
 * application components.</p>
 *
 * @version $Id: RowSetDynaClass.java 1540186 2013-11-08 21:08:30Z oheger $
 */

public class RowSetDynaClass extends JDBCDynaClass implements DynaClass, Serializable {


    // ----------------------------------------------------- Instance variables

    /**
     * <p>Limits the size of the returned list.  The call to
     * <code>getRows()</code> will return at most limit number of rows.
     * If less than or equal to 0, does not limit the size of the result.
     */
    protected int limit = -1;

    /**
     * <p>The list of {@link DynaBean}s representing the contents of
     * the original <code>ResultSet</code> on which this
     * {@link RowSetDynaClass} was based.</p>
     */
    protected List<DynaBean> rows = new ArrayList<DynaBean>();

    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to column names in the result set will be lower cased.</p>
     *
     * @param resultSet The result set to be wrapped
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet) throws SQLException {

        this(resultSet, true, -1);

    }

    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to column names in the result set will be lower cased.</p>
     * <p>
     * If <code>limit</code> is not less than 0, max <code>limit</code>
     * number of rows will be copied into the list.
     *
     * @param resultSet The result set to be wrapped
     * @param limit     The maximum for the size of the result.
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet, int limit) throws SQLException {

        this(resultSet, true, limit);

    }


    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to the column names in the result set will be lower cased or not,
     * depending on the specified <code>lowerCase</code> value.</p>
     * <p>
     * If <code>limit</code> is not less than 0, max <code>limit</code>
     * number of rows will be copied into the resultset.
     *
     * @param resultSet The result set to be wrapped
     * @param lowerCase Should property names be lower cased?
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase)
            throws SQLException {
        this(resultSet, lowerCase, -1);

    }

    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
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
     * @param limit     Maximum limit for the <code>List</code> of {@link DynaBean}
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, int limit)
            throws SQLException {

        this(resultSet, lowerCase, limit, false);

    }

    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
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
    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, boolean useColumnLabel)
            throws SQLException {
        this(resultSet, lowerCase, -1, useColumnLabel);

    }

    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
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
     * @param limit          Maximum limit for the <code>List</code> of {@link DynaBean}
     * @param useColumnLabel true if the column label should be used, otherwise false
     * @throws NullPointerException if <code>resultSet</code>
     *                              is <code>null</code>
     * @throws SQLException         if the metadata for this result set
     *                              cannot be introspected
     * @since 1.8.3
     */
    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, int limit, boolean useColumnLabel)
            throws SQLException {

        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.lowerCase = lowerCase;
        this.limit = limit;
        setUseColumnLabel(useColumnLabel);
        introspect(resultSet);
        copy(resultSet);

    }

    /**
     * <p>Return a <code>List</code> containing the {@link DynaBean}s that
     * represent the contents of each <code>Row</code> from the
     * <code>ResultSet</code> that was the basis of this
     * {@link RowSetDynaClass} instance.  These {@link DynaBean}s are
     * disconnected from the database itself, so there is no problem with
     * modifying the contents of the list, or the values of the properties
     * of these {@link DynaBean}s.  However, it is the application's
     * responsibility to persist any such changes back to the database,
     * if it so desires.</p>
     *
     * @return A <code>List</code> of {@link DynaBean} instances
     */
    public List<DynaBean> getRows() {

        return (this.rows);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * <p>Copy the column values for each row in the specified
     * <code>ResultSet</code> into a newly created {@link DynaBean}, and add
     * this bean to the list of {@link DynaBean}s that will later by
     * returned by a call to <code>getRows()</code>.</p>
     *
     * @param resultSet The <code>ResultSet</code> whose data is to be
     *                  copied
     * @throws SQLException if an error is encountered copying the data
     */
    protected void copy(ResultSet resultSet) throws SQLException {

        int cnt = 0;
        while (resultSet.next() && (limit < 0 || cnt++ < limit)) {
            DynaBean bean = createDynaBean();
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                Object value = getObject(resultSet, name);
                bean.set(name, value);
            }
            rows.add(bean);
        }

    }


    /**
     * <p>Create and return a new {@link DynaBean} instance to be used for
     * representing a row in the underlying result set.</p>
     *
     * @return A new <code>DynaBean</code> instance
     */
    protected DynaBean createDynaBean() {

        return (new BasicDynaBean(this));

    }


}
