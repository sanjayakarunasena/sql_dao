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
package tech.sqldao.test.dao;

import java.sql.Types;
import java.util.List;

import tech.sqldao.AbstractDAO;
import tech.sqldao.DAOTransientException;
import tech.sqldao.annotation.DaoMethods;
import tech.sqldao.annotation.Query;
import tech.sqldao.annotation.Result;
import tech.sqldao.annotation.DaoMethods.DaoMethod;
import tech.sqldao.annotation.Query.ParameterType;
import tech.sqldao.annotation.Result.Column;
import tech.sqldao.dbcon.DBConfigException;
import tech.sqldao.test.data.PersonInfo;

/**
 * @author Sanjaya Karunasena
 *
 */
@DaoMethods(methods = { @DaoMethod(index = 0, name = "getPersonInfo"),
        @DaoMethod(index = 1, name = "getPersonInfo", paramTypes = { int.class }),
        @DaoMethod(index = 2, name = "getPersonIds") })
public class PersonInfoDAO extends AbstractDAO {
    /**
     * Select all person info records.
     * 
     * @return List of person info.
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    @Query(sql = "SELECT id, name, age, email, city FROM person_info")
    @Result(columns = { @Column(name = "id", field = "id", type = Integer.class),
            @Column(name = "name", field = "name", type = String.class),
            @Column(name = "age", field = "age", type = Integer.class),
            @Column(name = "email", field = "email", type = String.class),
            @Column(name = "city", field = "city", type = String.class) })
    public List<PersonInfo> getPersonInfo() throws DAOTransientException, DBConfigException {
        return findAll(0, PersonInfo.class);
    }

    /**
     * Select a person info record by id.
     * 
     * @param id
     * @return A person info.
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    @Query(sql = "SELECT id, name, age, email, city FROM person_info WHERE id=?",
            paramTypes = { @ParameterType(index = 1, sqlType = Types.INTEGER) })
    @Result(columns = { @Column(name = "id", field = "id", type = Integer.class),
            @Column(name = "name", field = "name", type = String.class),
            @Column(name = "age", field = "age", type = Integer.class),
            @Column(name = "email", field = "email", type = String.class),
            @Column(name = "city", field = "city", type = String.class) })
    public PersonInfo getPersonInfo(int id) throws DAOTransientException, DBConfigException {
        return find(1, PersonInfo.class, id);
    }

    /**
     * Select a person info record ids.
     * 
     * @return All person info record ids.
     * @throws DAOTransientException
     * @throws DBConfigException
     */
    @Query(sql = "SELECT id FROM person_info")
    @Result(encapsulate = false, columns = { @Column(name = "id", field = "", type = Integer.class) })
    public List<Integer> getPersonIds() throws DAOTransientException, DBConfigException {
        return findAll(2, Integer.class);
    }

    /**
     * @see tech.sqldao.AbstractDAO#getClazz()
     */
    @Override
    protected Class<?> getClazz() {
        return PersonInfoDAO.class;
    }
}
