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
package tech.sqldao.test;

import tech.sqldao.test.util.DbUtil;
import junit.framework.TestCase;

/**
 * @author Sanjaya Karunasena
 *
 */
abstract public class AbstractQueryTest extends TestCase {
    /**
     * Set up the test case by creating a database connection to the test database.
     */
    @Override
    protected void setUp() throws Exception {
        DbUtil.setupDatabase();
        DbUtil.createTestSchema();
        DbUtil.createTestData();
    }

    /**
     * Concrete test cases should implement this method to test each of the scenarios.
     * 
     * @throws Exception
     */
    abstract public void testQuery() throws Exception;

    /**
     * Tear down the test case by closing the database connection.
     */
    @Override
    protected void tearDown() throws Exception {
        DbUtil.cleanupDatabase();
    }
}
