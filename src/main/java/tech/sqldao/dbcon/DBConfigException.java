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

/**
 * @author Sanjaya Karunasena
 *
 */
public class DBConfigException extends Exception {

    /**
     * Auto generated serial version UID
     */
    private static final long serialVersionUID = -7264177866201997009L;

    public DBConfigException(String message) {
        super(message);
    }

    public DBConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
