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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tech.sqldao.config.Environment;
import tech.sqldao.config.SimpleXMLPropertyException;
import tech.sqldao.config.SimpleXMLPropertyFile;

/**
 * @author Sanjaya Karunasena
 */
public class DBConfig {
    public static final String PROPERTY_FILE = "conf/db_config.xml";
    public static final String DB_SERVER_ADDRESS = "DBServerAddress";
    public static final String DB_SERVER_PORT = "DBServerPort";
    public static final String DB_NAME = "DBName";
    public static final String DB_USER = "DBUser";
    public static final String DB_PASSWORD = "DBPassword";
    public static final String CON_TIMEOUT = "ConTimeout";
    public static final String MIN_IDLE_CONS = "MinIdleCons";
    public static final String MAX_POOL_SIZE = "MaxPoolSize";
    public static final String POOL_NAME = "PoolName";
    public static final String TX_ISOLATION = "TxIsolation";

    private String[] serverAddresses;
    private String dbName;
    private String dbUser;
    private String dbPasswd;
    private int conTimeout;
    private byte minIdleCons;
    private int maxPoolSize;
    private String poolName;
    private String txIsolation;

    /**
     * This should be constructed using loadDBConfig
     */
    private DBConfig() {
    }

    public static DBConfig loadDBConfig() throws DBConfigException {
        // Get the server home directory
        Environment env = Environment.getEnvironment();
        try {
            // Load the DB configurations
            File dbPropertyFile = new File(env.getHomeDir() + "/" + DBConfig.PROPERTY_FILE);
            SimpleXMLPropertyFile propertyFile = SimpleXMLPropertyFile.load(dbPropertyFile);
            // Let's create the DB properties
            DBConfig dbConfig = new DBConfig();
            dbConfig.setServerAddresses(propertyFile.getValues(DBConfig.DB_SERVER_ADDRESS));
            dbConfig.setDbName(propertyFile.getValue(DBConfig.DB_NAME));
            dbConfig.setDbUser(propertyFile.getValue(DBConfig.DB_USER));
            dbConfig.setDbPasswd(propertyFile.getValue(DBConfig.DB_PASSWORD));
            dbConfig.setConTimeout(propertyFile.getValue(DBConfig.CON_TIMEOUT));
            dbConfig.setMinIdleCons(propertyFile.getValue(DBConfig.MIN_IDLE_CONS));
            dbConfig.setMaxPoolSize(propertyFile.getValue(DBConfig.MAX_POOL_SIZE));
            dbConfig.setPoolName(propertyFile.getValue(DBConfig.POOL_NAME));
            dbConfig.setTxIsolation(propertyFile.getValue(DBConfig.TX_ISOLATION));

            return dbConfig;
        } catch (FileNotFoundException e) {
            throw new DBConfigException("Unable to find the DB property file: " + env.getHomeDir() + "/"
                    + DBConfig.PROPERTY_FILE, e);
        } catch (IOException e) {
            throw new DBConfigException("Error reading the DB property file: " + env.getHomeDir() + "/"
                    + DBConfig.PROPERTY_FILE, e);
        } catch (ParserConfigurationException e) {
            throw new DBConfigException("Unable to create a XML parser", e);
        } catch (SAXException e) {
            throw new DBConfigException("Unable to pass the database configurations from: " + env.getHomeDir() + "/"
                    + DBConfig.PROPERTY_FILE, e);
        } catch (SimpleXMLPropertyException e) {
            throw new DBConfigException("Missing configuration element in: " + env.getHomeDir() + "/"
                    + DBConfig.PROPERTY_FILE, e);
        }
    }

    /**
     * @return the serverAddresses
     */
    public String[] getServerAddresses() {
        return serverAddresses;
    }

    /**
     * @param serverAddresses
     *            the serverAddresses to set
     */
    private void setServerAddresses(String[] serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName
     *            the dbName to set
     */
    private void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the dbUser
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * @param dbUser
     *            the dbUser to set
     */
    private void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * @return the dbPasswd
     */
    public String getDbPasswd() {
        return dbPasswd;
    }

    /**
     * @param dbPasswd
     *            the dbPasswd to set
     */
    private void setDbPasswd(String dbPasswd) {
        this.dbPasswd = dbPasswd;
    }

    /**
     * @return the conTimeout
     */
    public int getConTimeout() {
        return conTimeout;
    }

    /**
     * @param conTimeout
     *            the conTimeout to set
     */
    private void setConTimeout(String conTimeout) {
        this.conTimeout = Integer.parseInt(conTimeout);
    }

    /**
     * @return the minIdleCons
     */
    public int getMinIdleCons() {
        return minIdleCons;
    }

    /**
     * @param minIdleCons
     *            the minIdleCons to set
     */
    private void setMinIdleCons(String minIdleCons) {
        this.minIdleCons = Byte.parseByte(minIdleCons);
    }

    /**
     * @return the maxPoolSize
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * @param maxPoolSize
     *            the maxPoolSize to set
     */
    private void setMaxPoolSize(String maxPoolSize) {
        this.maxPoolSize = Integer.parseInt(maxPoolSize);
    }

    /**
     * @return the poolName
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * @param poolName
     *            the poolName to set
     */
    private void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * @return the txIsolation
     */
    public String getTxIsolation() {
        return txIsolation;
    }

    /**
     * @param txIsolation
     *            the txIsolation to set
     */
    private void setTxIsolation(String txIsolation) {
        this.txIsolation = txIsolation;
    }
}
