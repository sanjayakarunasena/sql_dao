/**
 * Copyright 2015 Sanjaya Karunasena
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.sqldao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.sqldao.annotation.DaoMethods;
import tech.sqldao.annotation.DaoMethods.DaoMethod;
import tech.sqldao.annotation.Query;
import tech.sqldao.annotation.Query.ParameterType;
import tech.sqldao.annotation.Result;
import tech.sqldao.annotation.Result.Column;
import tech.sqldao.dbcon.DBConfigException;
import tech.sqldao.dbcon.DBConnectionManager;

/**
 * All DAO classes should extend this class. All methods which performs database operations should be defined at the
 * class level using @DaoMethods annotation. Each @DaoMethod's index should match the method index parameter value
 * passed into the super class (i.e. this class) method. The SQL query to be used by each method should be defined at
 * the method level using @Query annotation. If the query returns a result set, result set mapping to result class type
 * should be defined using @Result annotation. If the result set contains multiple columns, it should be encapsulated
 * into a class defined as a value object.
 * <p>
 * Before using this class ensure that a {@link DataSource} is initialised using
 * {@link DBConnectionManager#init(DataSource)} or {@link DBConnectionManager#init(String)}.
 * </p>
 * 
 * @author Sanjaya Karunasena
 *
 */
public abstract class AbstractDAO {
    /**
     * Encapsulate IN clause values which need to be used in a prepared statement if the SQL statement has an IN clause.
     * We need to use a specific algorithm to build the SQL statement with an IN clause and set the values it.
     * 
     * @author Sanjaya Karunasena
     *
     */
    public static class InClauseValues<V> {
        private List<V> values;

        /**
         * Convenient constructor.
         * 
         * @param values
         *            IN clause values to use.
         */
        public InClauseValues(List<V> values) {
            this.values = values;
        }

        /**
         * @return List of values.
         */
        public List<V> get() {
            return values;
        }

        /**
         * @return Count of values.
         */
        public int count() {
            if (values != null) {
                return values.size();
            } else {
                return 0;
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);
    private Connection con;
    private int retryCount;

    /**
     * Default constructor.
     */
    protected AbstractDAO() {
        this.retryCount = 3;
    }

    /**
     * Set the {@link Connection} to use.
     * 
     * @param con
     *            {@link Connection}
     */
    protected void setConnection(Connection con) {
        this.con = con;
    }

    /**
     * Set the retry count in case of a {@link SQLTransientException}. The default value is 3.
     * 
     * @param retryCount
     *            Retry count
     */
    protected void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * Find an object in the database and populate it only with the corresponding values specified by column names
     * defined using the {@link Result} annotation by the sub class. The sql query to be executed should be defined
     * using the {@link Query} annotation by the sub class.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} and the {@link Result} from the
     *            {@link DaoMethods} for this method.
     * @param resultType
     *            As per {@link Result} annotation, the fields in an instance of this type is populated and returned.
     * @param paramValues
     *            Values of the query parameters defined in {@link Query}.
     * @return T object
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    protected <T> T find(int methodIndex, Class<T> resultType, Object... paramValues) throws DAOTransientException,
            DBConfigException {
        return find(retryCount, methodIndex, resultType, paramValues);
    }

    /**
     * Find an object in the database and populate it only with the corresponding values specified by column names
     * defined using the {@link Result} annotation by the sub class. The sql query to be executed should be defined
     * using the {@link Query} annotation by the sub class.
     * 
     * @param retryOnTransientException
     *            Whether to retry if a transient exception occurred.
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} and the {@link Result} from the
     *            {@link DaoMethods} for this method.
     * @param resultType
     *            As per {@link Result} annotation, the fields in an instance of this type is populated and returned.
     * @param paramValues
     *            Values of the query parameters defined in {@link Query}.
     * @return T object
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    private <T> T find(int retryCountOnTransientException, int methodIndex, Class<T> resultType, Object... paramValues)
            throws DAOTransientException, DBConfigException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Find the method corresponding the query intended to execute.
        Method method = getQueryMethod(getClazz(), methodIndex);
        try {
            if (con == null || con.isClosed()) {
                con = getDBConnection();
            }
            // Read annotations and discover the query
            Query query = getQuery(method);
            ps = prepareStatement(con, query, paramValues, query.paramTypes());
            rs = ps.executeQuery();
            return getValueObj(resultType, rs, getEncapsulate(method), getResultColumns(method));
        } catch (SQLTransientException sqlte) {
            if (retryCountOnTransientException <= 0) {
                LOG.warn(CommonMsg.WARN_TRANSIENT_EXCEPTION, sqlte);
                // Retry "retry count" times if a transient exception occurred after a little delay.
                ThreadUtil.delay(500);
                return find(retryCountOnTransientException--, methodIndex, resultType, paramValues);
            } else {
                throw new DAOTransientException(CommonMsg.ERROR_DATA_RETRIEVE_AFTER_RETRY, sqlte);
            }
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_DATA_RETRIEVE, sqle);
        } finally {
            cleanUp(con, ps, rs);
        }
    }

    /**
     * Find multiple objects in the database and populate them only with the corresponding values specified by column
     * names defined using the {@link Result} annotation by the sub class. The sql query to be executed should be
     * defined using the {@link Query} annotation by the sub class.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} and the {@link Result} from the
     *            {@link DaoMethods} for this method.
     * @param resultElementType
     *            As per {@link Result} annotation, the fields in an instance of this type is populated and a list of
     *            them is returned.
     * @param paramValues
     *            Query parameter values
     * @return List of T object
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    protected <T> List<T> findAll(int methodIndex, Class<T> resultElementType, Object... paramValues)
            throws DAOTransientException, DBConfigException {
        return findAll(retryCount, methodIndex, resultElementType, paramValues);
    }

    /**
     * Find multiple objects in the database and populate them only with the corresponding values specified by column
     * names defined using the {@link Result} annotation by the sub class. The sql query to be executed should be
     * defined using the {@link Query} annotation by the sub class.
     * 
     * @param retryCountOnTransientException
     *            Whether to retry if a transient exception occurred.
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} and the {@link Result} from the
     *            {@link DaoMethods} for this method.
     * @param resultElementType
     *            As per {@link Result} annotation, the fields in an instance of this type is populated and a list of
     *            them is returned.
     * @param paramValues
     *            Query parameter values
     * @return List of T object
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    private <T> List<T> findAll(int retryCountOnTransientException, int methodIndex, Class<T> resultElementType,
            Object... paramValues) throws DAOTransientException, DBConfigException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Find the method corresponding the query intend to execute.
        Method method = getQueryMethod(getClazz(), methodIndex);
        try {
            if (con == null || con.isClosed()) {
                con = getDBConnection();
            }
            // Read annotations and discover the query
            Query query = getQuery(method);
            ps = prepareStatement(con, query, paramValues, query.paramTypes());
            rs = ps.executeQuery();
            return getValueObjs(resultElementType, rs, getEncapsulate(method), getResultColumns(method));
        } catch (SQLTransientException sqlte) {
            if (retryCountOnTransientException <= 0) {
                LOG.warn(CommonMsg.WARN_TRANSIENT_EXCEPTION, sqlte);
                // Retry "retry count" times if a transient exception occurred after a little delay.
                ThreadUtil.delay(500);
                return findAll(retryCountOnTransientException--, methodIndex, resultElementType, paramValues);
            } else {
                throw new DAOTransientException(CommonMsg.ERROR_DATA_RETRIEVE_AFTER_RETRY, sqlte);
            }
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_DATA_RETRIEVE, sqle);
        } finally {
            cleanUp(con, ps, rs);
        }
    }

    /**
     * Create an object in the database.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    protected void create(int methodIndex, Object... paramValues) throws DAOTransientException, DBConfigException {
        create(retryCount, methodIndex, paramValues);
    }

    /**
     * Create an object in the database.
     * 
     * @param retryCountOnTransientException
     *            Whether to retry if a transient exception occurred.
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    private void create(int retryCountOnTransientException, int methodIndex, Object... paramValues)
            throws DAOTransientException, DBConfigException {
        PreparedStatement ps = null;
        // Find the method corresponding the query intend to execute.
        Method method = getQueryMethod(getClazz(), methodIndex);
        try {
            if (con == null || con.isClosed()) {
                con = getDBConnection();
            }
            // Read annotations and discover the query
            Query query = getQuery(method);
            ps = prepareStatement(con, query, paramValues, query.paramTypes());
            ps.executeUpdate();
        } catch (SQLTransientException sqlte) {
            if (retryCountOnTransientException <= 0) {
                LOG.warn(CommonMsg.WARN_TRANSIENT_EXCEPTION, sqlte);
                // Retry "retry count" times if a transient exception occurred after a little delay.
                ThreadUtil.delay(500);
                create(retryCountOnTransientException--, methodIndex, paramValues);
            } else {
                throw new DAOTransientException(CommonMsg.ERROR_DATA_INSERT_AFTER_RETRY, sqlte);
            }
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_DATA_INSERT, sqle);
        } finally {
            cleanUp(con, ps);
        }
    }

    /**
     * Update an object in the database.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @return Number of rows updated
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    protected int update(int methodIndex, Object... paramValues) throws DAOTransientException, DBConfigException {
        return update(retryCount, methodIndex, paramValues);
    }

    /**
     * Update an object in the database.
     * 
     * @param retryCountOnTransientException
     *            Whether to retry if a transient exception occurred.
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @return Number of rows updated
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    private int update(int retryCountOnTransientException, int methodIndex, Object... paramValues)
            throws DAOTransientException, DBConfigException {
        try {
            return updateOrDelete(methodIndex, paramValues);
        } catch (SQLTransientException sqlte) {
            if (retryCountOnTransientException <= 0) {
                LOG.warn(CommonMsg.WARN_TRANSIENT_EXCEPTION, sqlte);
                // Retry "retry count" times if a transient exception occurred after a little delay.
                ThreadUtil.delay(500);
                return update(retryCountOnTransientException--, methodIndex, paramValues);
            } else {
                throw new DAOTransientException(CommonMsg.ERROR_DATA_UPDATE_AFTER_RETRY, sqlte);
            }
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_DATA_UPDATE, sqle);
        }
    }

    /**
     * Delete an object in the database.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @return Number of rows deleted
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    protected int delete(int methodIndex, Object... paramValues) throws DAOTransientException, DBConfigException {
        return delete(retryCount, methodIndex, paramValues);
    }

    /**
     * Delete an object in the database.
     * 
     * @param retryCountOnTransientException
     *            Whether to retry if a transient exception occurred.
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @return Number of rows deleted
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    private int delete(int retryCountOnTransientException, int methodIndex, Object... paramValues)
            throws DAOTransientException, DBConfigException {
        try {
            return updateOrDelete(methodIndex, paramValues);
        } catch (SQLTransientException sqlte) {
            if (retryCountOnTransientException <= 0) {
                LOG.warn(CommonMsg.WARN_TRANSIENT_EXCEPTION, sqlte);
                // Retry "retry count" times if a transient exception occurred after a little delay.
                ThreadUtil.delay(500);
                return delete(retryCountOnTransientException--, methodIndex, paramValues);
            } else {
                throw new DAOTransientException(CommonMsg.ERROR_DATA_DELETE_AFTER_RETRY, sqlte);
            }
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_DATA_DELETE, sqle);
        }
    }

    /**
     * Update or delete an object in the database.
     * 
     * @param methodIndex
     *            Query map key to discover the corresponding {@link Query} from the {@link DaoMethods} for this method.
     * @param paramValues
     *            Query parameter values
     * @return Number of rows updated or deleted
     * @throws SQLException
     * @throws DBConfigException
     */
    private int updateOrDelete(int methodIndex, Object... paramValues) throws SQLException, DBConfigException {
        PreparedStatement ps = null;
        // Find the method corresponding the query intend to execute.
        Method method = getQueryMethod(getClazz(), methodIndex);
        try {
            if (con == null || con.isClosed()) {
                con = getDBConnection();
            }
            // Read annotations and discover the query
            Query query = getQuery(method);
            ps = prepareStatement(con, query, paramValues, query.paramTypes());
            return ps.executeUpdate();
        } finally {
            cleanUp(con, ps);
        }
    }

    /**
     * Get a {@link Connection} object
     * 
     * @return {@link Connection} object
     * @throws SQLException
     */
    private Connection getDBConnection() throws SQLException {
        return DBConnectionManager.getDBCon();
    }

    /**
     * Create a {@link PreparedStatement} object.
     * 
     * @param con
     *            {@link Connection} object.
     * @param sql
     *            SQL query
     * @param parameters
     *            Query parameters
     * @return {@link PreparedStatement} object
     * @throws SQLException
     */
    private PreparedStatement prepareStatement(Connection con, Query query, Object[] paramValues,
            ParameterType[] paramTypes) throws SQLException {
        String sql = getSqlQuery(query, paramValues);
        PreparedStatement ps = con.prepareStatement(sql);
        setParameters(ps, paramValues, paramTypes);

        if (LOG.isDebugEnabled()) {
            LOG.error("SQL: " + ps.toString());
        }

        return ps;
    }

    /**
     * Set given parameters to the prepared statement in the given order.
     * 
     * @param ps
     *            {@link PreparedStatement} object
     * @param parameters
     *            Array of parameters
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, Object[] paramValues, ParameterType[] paramTypes)
            throws SQLException {
        // Maintain the shift in the parameter index if there is an IN clause
        int indexShift = 0;

        for (int i = 0; i < paramValues.length; i++) {
            if (paramTypes[i].index() != i + 1) {
                throw new DAORuntimeException(
                        "@ParameterType annotation index order does not match with the parameter value order.");
            }
            // Check for nullable values
            if (paramValues[i] != null) {
                if (paramValues[i] instanceof InClauseValues) {
                    InClauseValues<?> inClauseValues = (InClauseValues<?>) paramValues[i];
                    for (Object value : inClauseValues.get()) {
                        ps.setObject(paramTypes[i].index() + indexShift, value, paramTypes[i].sqlType());
                        // Parameter index need to be shifted to accommodate the dynamic values came with the IN clause.
                        indexShift++;
                    }
                } else {
                    ps.setObject(paramTypes[i].index() + indexShift, paramValues[i], paramTypes[i].sqlType());
                }
            } else if (paramTypes[i].nullable()) {
                ps.setNull(paramTypes[i].index() + indexShift, paramTypes[i].sqlType());
            } else {
                throw new DAORuntimeException("Parameter " + paramTypes[i].index() + " cannot be null.");
            }
        }
    }

    /**
     * Map the values in the {@link ResultSet} to a T object. Concrete DAO class should implement the method
     * {@link #createValueObj(ResultSet, String...)} for this method work accurately.
     * 
     * @param resultType
     *            As per list of {@link Column} annotation the fields in an instance of this type is populated and
     *            returned.
     * @param rs
     *            {@link ResultSet} object
     * @param encapsulate
     *            Whether to encapsulate the column values in a result set raw.
     * @param columns
     *            Columns available in the result set.
     * @return T object. If there is no result, return null.
     * @throws SQLException
     */
    private <T> T getValueObj(Class<T> resultType, ResultSet rs, boolean encapsulate, Column... columns)
            throws SQLException {
        T t = null;
        // Check whether there are any results available.
        if (rs.next()) {
            t = createValueObj(resultType, rs, encapsulate, columns);
        }

        return t;
    }

    /**
     * Map the values in the {@link ResultSet} to a list of T objects. Concrete DAO class should implement the method
     * {@link #createValueObj(ResultSet, String...)} for this method work accurately.
     * 
     * @param resultElementType
     *            As per list of {@link Column} annotation the fields in an instance of this type is populated and a
     *            list of them is returned.
     * @param rs
     *            {@link ResultSet} object
     * @param encapsulate
     *            Whether to encapsulate the column values in a result set raw.
     * @param columns
     *            Columns available in the result set.
     * @return List of T objects. If there is no result, returns an empty list.
     * @throws SQLException
     * @throws DAOTransientException
     */
    private <T> List<T> getValueObjs(Class<T> resultElementType, ResultSet rs, boolean encapsulate, Column... columns)
            throws SQLException, DAOTransientException {
        List<T> ts = new ArrayList<T>();

        while (rs.next()) {
            T t = createValueObj(resultElementType, rs, encapsulate, columns);
            ts.add(t);
        }

        return ts;
    }

    /**
     * Creates a value object from a raw in the result set. This method is used by {@link #getValueObj(ResultSet)} and
     * {@link #getValueObjs(ResultSet)}. Since result set iteration is handles by the above methods, this method should
     * assume that there is a valid data raw at the current result set position.
     * 
     * @param resultType
     *            As per list of {@link Column} annotation the fields in an instance of this type is populated and
     *            returned.
     * @param rs
     *            {@link ResultSet} instance positioned at a valid data raw.
     * @param encapsulate
     *            Whether to encapsulate the column values in a result set raw.
     * @param columns
     *            Columns available in this result set.
     * @return Instance of {@link T} populated with values given by columnNames.
     * @throws SQLException
     */
    private <T> T createValueObj(Class<T> resultType, ResultSet rs, boolean encapsulate, Column... columns)
            throws SQLException {
        try {
            T t = null;

            if (encapsulate) {
                // Column values the result set raw need to be encapsulated within an instance of T.
                if (resultType.isPrimitive()) {
                    throw new DAORuntimeException("Unable to encapsulate into the primitive type "
                            + resultType.getName());
                }
                t = resultType.newInstance();
                // Go through each column and extract the result value
                for (Column column : columns) {
                    Field field = getDeclaredField(resultType, column.field());
                    field.setAccessible(true);

                    mapColumnToField(field, t, rs, column);
                }
            } else {
                // Simply use the first column value in the current result raw as the t object.
                t = (T) getColumnValue(rs, columns[0]);
            }

            return t;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new DAORuntimeException("Unable to create the value object and update the fields using "
                    + resultType.getName(), e);
        }
    }

    /**
     * Lookup recursively for the given field in the class inheritance hierarchy.
     * 
     * @param clazz
     *            Class
     * @param fieldName
     *            Field name to lookup
     * @return Return the {@link Field}
     * @throws NoSuchFieldException
     */
    private Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            // Try to find the field in the clazz
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsme) {
            // The field may be in the super class if there is a super class
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                return getDeclaredField(superClazz, fieldName);
            } else {
                throw nsme;
            }
        }
    }

    /**
     * Map column in the result set to a field in the given class object.
     * 
     * @param field
     *            Class field to map the column.
     * @param obj
     *            Class instance.
     * @param rs
     *            Result set.
     * @param column
     *            Result set column.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private void mapColumnToField(Field field, Object obj, ResultSet rs, Column column)
            throws IllegalArgumentException, IllegalAccessException, SQLException {
        field.set(obj, getColumnValue(rs, column));
    }

    /**
     * Get the column value.
     * 
     * @param rs
     *            Result set.
     * @param column
     *            Result set column.
     * @throws SQLException
     */
    private Object getColumnValue(ResultSet rs, Column column) throws SQLException {
        return rs.getObject(column.name(), column.type());
    }

    /**
     * Close the connection and statement objects.
     * 
     * @param con
     *            {@link Connection} object
     * @param st
     *            {@link Statement} object
     */
    private void cleanUp(Connection con, Statement st) {
        try {
            if (st != null) {
                st.close();
            }
            // If the DAO is participating in a transaction it should be closed by the transaction object.
            if (con != null && con.getAutoCommit() == true) {
                con.close();
            }
        } catch (SQLException sqle) {
            LOG.error(CommonMsg.ERROR_DB_CON_RELEASE, sqle);
        }
    }

    /**
     * Close the connection, statement and result set objects.
     * 
     * @param con
     *            {@link Connection} object
     * @param st
     *            {@link Statement} object
     * @param rs
     *            {@link ResultSet} object
     */
    private void cleanUp(Connection con, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            cleanUp(con, st);
        } catch (SQLException sqle) {
            LOG.error(CommonMsg.ERROR_DB_CON_RELEASE, sqle);
        }
    }

    /**
     * Sub class should implement this method and return its class type.
     * 
     * <pre>
     * <blockquote>
     * {@literal @}Override
     * protected Class&lt;SubClass&gt; getClazz() {
     *     return SubClass.class;
     * }
     * </blockquote>
     * </pre>
     * 
     * @return Class type.
     */
    protected abstract Class<?> getClazz();

    /**
     * Get the DAO class method to the corresponding SQL query.
     * 
     * @param subClazz
     *            Sub class type which has the annotation.
     * @param methodIndex
     *            Query map key specified by the sub class method.
     * @return The DAO method of the sub class.
     */
    private Method getQueryMethod(Class<?> subClazz, int methodIndex) {
        DaoMethods daoMethods = subClazz.getAnnotation(DaoMethods.class);
        if (daoMethods == null) {
            throw new DAORuntimeException("Sql query to method mapping must be specified using @DaoMethods annotation.");
        }
        if (methodIndex < 0 || daoMethods.methods().length <= methodIndex) {
            throw new DAORuntimeException("Invalid methodIndex: " + methodIndex);
        }

        DaoMethod method = daoMethods.methods()[methodIndex];
        if (method.index() != methodIndex) {
            throw new DAORuntimeException(
                    "@DaoMethod annotation index does not match with the methodIndex used by calling method "
                            + method.name() + " in " + subClazz.getName());
        }

        try {
            Method declaredMethod = this.getClass().getDeclaredMethod(method.name(), method.paramTypes());
            declaredMethod.setAccessible(true);

            return declaredMethod;
        } catch (NoSuchMethodException nsme) {
            throw new DAORuntimeException("Missing DAO class method.", nsme);
        }
    }

    /**
     * Get the sql query to be executed. If it has an IN clause it will construct the proper sql to be used in the
     * prepared statement. Currently this only support one IN clause and it should be written as "IN (?..?)".
     * 
     * @param query
     *            Annotated query to be used.
     * @param paramValues
     *            Parameter values to be used with the query.
     * @return {@link Query}.
     */
    private String getSqlQuery(Query query, Object[] paramValues) {
        String sqlQuery = query.sql();

        if (query.hasInClause()) {
            // There should be at least one parameter
            String inClauseParams = "?";
            for (Object paramValue : paramValues) {
                if (paramValue instanceof InClauseValues) {
                    InClauseValues<?> inClauseValues = (InClauseValues<?>) paramValue;
                    // inClauseParam already has the first ? and add in additional ?s.
                    for (int i = 1; i < inClauseValues.count(); i++) {
                        inClauseParams += ", ?";
                    }
                }
            }

            sqlQuery = sqlQuery.replace("?..?", inClauseParams);
        }

        return sqlQuery;
    }

    /**
     * Get the sql query information from the annotated element which is a method of a sub class.
     * 
     * @param method
     *            Annotated method.
     * @return {@link Query}.
     */
    private Query getQuery(Method method) {
        Query query = method.getAnnotation(Query.class);
        if (query == null) {
            throw new DAORuntimeException("Sql query must be specified using @Query annotation.");
        }

        return query;
    }

    /**
     * If the result contains multiple columns this should be true and the columns values should be encapsulated in the
     * class specified by resultType. If the result contains just one column and there is no requirement to encapsulate
     * the value this should be false.
     * 
     * @param method
     *            DAO method.
     * @return Whether to encapsulate result columns in an instance of the class specified by resultType.
     */
    private boolean getEncapsulate(Method method) {
        Result result = method.getAnnotation(Result.class);
        if (result == null) {
            throw new DAORuntimeException("Result columns must be specified using @Result annotation.");
        }

        return result.encapsulate();
    }

    /**
     * Get the Java class field to column mapping from the annotated element which is a method of a sub class.
     * 
     * @param method
     *            DAO method.
     * @return {@link Column} list.
     */
    private Column[] getResultColumns(Method method) {
        Result result = method.getAnnotation(Result.class);
        if (result == null) {
            throw new DAORuntimeException("Result columns must be specified using @Result annotation.");
        }

        return result.columns();
    }
}
