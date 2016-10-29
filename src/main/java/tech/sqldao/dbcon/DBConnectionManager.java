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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
     * Initialise the data source for the connection pool from a JNDI configuration,
     * "java:comp/env/jdbc/auth_server_ds".
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

    /**
     * Setup the data source for the connection pool from the $APPLICATION_HOME/conf/db_config.xml file.
     * 
     * @throws DBConfigException
     * 
     * @Deprecated {@link Datasource} should be provided by the application or the container.
     */
    @Deprecated
    public static synchronized void setup() throws DBConfigException {
        if (dataSource != null) {
            LOG.warn("Data source is already setup for this java runtime instance.");
            return;
        }

        DBConfig dbConf = DBConfig.loadDBConfig();

        String jdbcUrl = createJdbcUrl(dbConf);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbConf.getDbUser());
        config.setPassword(dbConf.getDbPasswd());
        config.setConnectionTimeout(dbConf.getConTimeout() * 1000);
        config.setMinimumIdle(dbConf.getMinIdleCons());
        config.setMaximumPoolSize(dbConf.getMaxPoolSize());
        config.setPoolName(dbConf.getPoolName());
        config.setTransactionIsolation(dbConf.getTxIsolation());

        dataSource = new HikariDataSource(config);
    }

    private static String createJdbcUrl(DBConfig dbConf) {
        String[] addresses = dbConf.getServerAddresses();
        String jdbcUrl;
        if (addresses.length == 1) {
            jdbcUrl = "jdbc:mysql://" + addresses[0];
        } else {
            jdbcUrl = "jdbc:mysql:loadbalance://" + addresses[0];
            for (int i = 1; i < addresses.length; i++) {
                jdbcUrl += ',' + addresses[i];
            }
        }
        jdbcUrl += '/' + dbConf.getDbName();

        return jdbcUrl;
    }

    /**
     * Clean up the database connection pool.
     * 
     * @deprecated Database connections should be clean up by the application or the container.
     */
    @Deprecated
    public static synchronized void cleanUp() {
        if (dataSource != null) {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
            // Set it to null so that it can be setup again if required
            dataSource = null;
        } else {
            LOG.warn("Data source is not setup. Did you call DBConnectionPoolManager.setup()?");
        }
    }
}
