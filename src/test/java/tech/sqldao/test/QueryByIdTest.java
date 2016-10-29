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

import tech.sqldao.test.dao.PersonInfoDAO;
import tech.sqldao.test.data.PersonInfo;

/**
 * @author Sanjaya Karunasena
 *
 */
public class QueryByIdTest extends AbstractQueryTest {

    /**
     * Simple query test.
     * 
     * @see tech.sqldao.test.AbstractQueryTest#testQuery()
     */
    @Override
    public void testQuery() throws Exception {
        PersonInfoDAO dao = new PersonInfoDAO();
        PersonInfo pInfo = dao.getPersonInfo(101);

        PersonInfo.print(pInfo);
    }

}
