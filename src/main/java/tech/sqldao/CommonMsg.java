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

/**
 * @author Sanjaya Karunasena
 *
 */
class CommonMsg {
    static final String SELECT_STATEMENT = "Select statement: ";
    static final String RESULT = "Result: ";
    static final String INSERT_STATEMENT = "Insert statement: ";
    static final String UPDATE_STATEMENT = "Update statement: ";
    static final String DELETE_STATEMENT = "Delete statement: ";

    static final String WARN_TRANSIENT_EXCEPTION = "Transient exception occurred: ";

    static final String ERROR_DATA_RETRIEVE = "Error in data retrieve: ";
    static final String ERROR_DATA_RETRIEVE_AFTER_RETRY = "Error in data retrieve after retry: ";
    static final String ERROR_DATA_INSERT = "Error in data insert: ";
    static final String ERROR_DATA_INSERT_AFTER_RETRY = "Error in data retrieve after retry: ";
    static final String ERROR_DATA_UPDATE = "Error in data update: ";
    static final String ERROR_DATA_UPDATE_AFTER_RETRY = "Error in data update after retry: ";
    static final String ERROR_DATA_INSERT_OR_UPDATE = "Error in data insert or update: ";
    static final String ERROR_DATA_INSERT_OR_UPDATE_AFTER_RETRY = "Error in data insert or update after retry: ";
    static final String ERROR_DATA_DELETE = "Error in data delete: ";
    static final String ERROR_DATA_DELETE_AFTER_RETRY = "Error in data delete after retry: ";
    static final String ERROR_DB_CONFIG = "Error in DB configuration";
    static final String ERROR_DB_CONNECTION = "Error getting a DB connection";
    static final String ERROR_DB_CON_RELEASE = "Error releasing DB connection";
    static final String ERROR_TX_BEGIN = "Unable to begin the transaction";
    static final String ERROR_TX_STARTED = "Transaction already started";
    static final String ERROR_TX_NOT_STARTED = "Transaction not started";
    static final String ERROR_TX_COMMIT = "Unable to commit the transaction";
    static final String ERROR_TX_ROLLBACK = "Unable to rollback the transaction";
    static final String ERROR_TX_END = "Unable to end the transaction";

    private CommonMsg() {
    }
}
