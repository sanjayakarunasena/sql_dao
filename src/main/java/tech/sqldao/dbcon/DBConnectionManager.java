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
package tech.sqldao.dbcon;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sanjaya Karunasena
 */
public class DBConnectionManager {
    private static DataSource dataSource = null;

    private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);

    /**
     * Return a database connection from the connection pool.
     * 
     * @return {@link Connection}
     * @throws SQLException
     * @throws DBConfigException
     */
    public static Connection getDBCon() throws SQLException, DBConfigException {
        if (dataSource == null) {
            throw new DBConfigException("DataSource is not setup. Did you call DBConnectionManager.init()?");
        }
        return dataSource.getConnection();
    }

    /**
     * Initialise the data source for the connection pool from a JNDI configuration, "java:comp/env/jdbc/my_server_ds".
     * 
     * @param datasource
     *            Datasource name (e.g. jdbc/mydatasource)
     * 
     * @throws DBConfigException
     */
    public static synchronized void init(String jndiDatasource) throws DBConfigException {
        if (DBConnectionManager.dataSource != null) {
            LOG.warn("Data source is already initialise for this instance.");
            return;
        }

        try {
            InitialContext ic = new InitialContext();
            DBConnectionManager.dataSource = (DataSource) ic.lookup("java:comp/env/" + jndiDatasource);
        } catch (NamingException ne) {
            throw new DBConfigException("JNDI Error!", ne);
        }
    }

    /**
     * Use an already initialised data source.
     * 
     * @param datasource
     *            Datasource which is already initialised by the application.
     * 
     * @throws DBConfigException
     */
    public static synchronized void init(DataSource dataSource) throws DBConfigException {
        if (DBConnectionManager.dataSource != null) {
            LOG.warn("Data source is already initialise for this instance.");
            return;
        }

        DBConnectionManager.dataSource = dataSource;
    }
}
