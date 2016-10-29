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

import java.sql.Connection;
import java.sql.SQLException;

import tech.sqldao.dbcon.DBConfigException;
import tech.sqldao.dbcon.DBConnectionManager;

/**
 * @author Sanjaya Karunasena
 *
 */
public class Transaction {
    private Connection con;

    /**
     * Start a transaction for which DAO instances can participate.
     * 
     * @return This transaction object.
     * @throws DBConfigException
     */
    public Transaction begin() throws DBConfigException {
        if (con != null) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_STARTED);
        }

        try {
            con = getDBConnection();
            con.setAutoCommit(false);
            return this;
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_BEGIN, sqle);
        }
    }

    /**
     * Get a DAO object to participate in this transaction.
     * 
     * @param dao
     *            DAO object
     * @return DAO object
     */
    public <U extends AbstractDAO> U participate(U dao) {
        if (con == null) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_NOT_STARTED);
        }

        dao.setConnection(con);
        return dao;
    }

    /**
     * Commit the transaction.
     * 
     * @return This transaction object.
     */
    public Transaction commit() {
        if (con == null) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_NOT_STARTED);
        }

        try {
            con.commit();
            return this;
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_COMMIT, sqle);
        }
    }

    /**
     * Rollback the transaction.
     * 
     * @return This transaction object.
     */
    public Transaction rollback() {
        if (con == null) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_NOT_STARTED);
        }

        try {
            con.rollback();
            return this;
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_COMMIT, sqle);
        }
    }

    /**
     * Complete the transaction.
     * 
     */
    public void end() {
        if (con == null) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_NOT_STARTED);
        }

        try {
            con.setAutoCommit(true);
            con.close();
        } catch (SQLException sqle) {
            throw new DAORuntimeException(CommonMsg.ERROR_TX_END, sqle);
        }
    }

    /**
     * Get a {@link Connection} object
     * 
     * @return {@link Connection} object
     * @throws DBConfigException
     * @throws SQLException
     */
    private Connection getDBConnection() throws SQLException, DBConfigException {
        return DBConnectionManager.getDBCon();
    }
}
