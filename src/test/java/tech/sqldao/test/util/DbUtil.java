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
package tech.sqldao.test.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import tech.sqldao.dbcon.DBConfigException;
import tech.sqldao.dbcon.DBConnectionManager;

/**
 * @author Sanjaya Karunasena
 *
 */
public class DbUtil {
    private static final String DB_NAME = "sql_dao_test";

    /**
     * Set up a {@link DataSource} to Apache Derby to test the project.
     * 
     * @throws DBConfigException
     * @throws SQLException
     */
    public static void setupDatabase() throws DBConfigException, SQLException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName(DB_NAME);
        dataSource.setCreateDatabase("create");
        DBConnectionManager.init(dataSource);
    }

    /**
     * Create a test schema to test the project. {@link DbUtil#setupDBConnectionManager()} should be called before
     * calling this method.
     * 
     * @throws SQLException
     * @throws DBConfigException
     */
    public static void createTestSchema() throws SQLException, DBConfigException {
        Connection con = DBConnectionManager.getDBCon();
        final String query =
                "CREATE TABLE person_info (id INTEGER NOT NULL, name VARCHAR(256) NOT NULL,"
                        + " age INTEGER, email VARCHAR(256), city VARCHAR(128), PRIMARY KEY (id))";

        Statement st = con.createStatement();
        try {
            st.executeUpdate(query);
        } catch (SQLException sqle) {
            /*
             * Derby does not support CREATE TABEL IF NOT EXISTS.... Therefore, check whether it is table already exist
             * error and ignore or through the exception.
             */
            if (!sqle.getSQLState().equals("X0Y32")) {
                throw sqle;
            }
        }
        st.close();
        con.close();
    }

    /**
     * Create a test data to test the project. {@link DbUtil#createTestSchema()} should be called before calling this
     * method.
     * 
     * @throws SQLException
     * @throws DBConfigException
     */
    public static void createTestData() throws SQLException, DBConfigException {
        Connection con = DBConnectionManager.getDBCon();
        final String query1 =
                "INSERT INTO person_info VALUES(101, 'Sanjaya Karunasena', 25, 'sanjaya@sanjayakarunasena.com', 'Colombo')";
        final String query2 =
                "INSERT INTO person_info VALUES(102, 'Chathura Welgama', 35, 'chathura@sanjayakarunasena.com', 'Galle')";

        Statement st = con.createStatement();
        try {
            st.executeUpdate(query1);
        } catch (SQLException sqle) {
            /*
             * Ignore duplicate key errors since we may have already created this record.
             */
            if (!sqle.getSQLState().equals("23505")) {
                throw sqle;
            }
        }
        try {
            st.executeUpdate(query2);
        } catch (SQLException sqle) {
            /*
             * Ignore duplicate key errors since we may have already created this record.
             */
            if (!sqle.getSQLState().equals("23505")) {
                throw sqle;
            }
        }
        st.close();
        con.close();
    }

    /**
     * Clean up the database connection.
     * 
     * @throws DBConfigException
     */
    public static void cleanupDatabase() throws DBConfigException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setShutdownDatabase("shutdown");
    }
}
