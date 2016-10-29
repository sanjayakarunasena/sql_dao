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
package tech.sqldao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.PreparedStatement;
import java.sql.Types;

import tech.sqldao.AbstractDAO;

/**
 * Annotation used to declare a database query. This is processed by {@link AbstractDAO} methods to identify the sql
 * query to be executed and the query parameters. Query parameters should be ordered as required by the corresponding
 * sql query and their index should be 1 based as defined by {@link PreparedStatement} API.
 * 
 * @author Sanjaya Karunasena
 *
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    /**
     * Annotation used to declare a query parameter type as defined in {@link Types}.
     * 
     * @author Sanjaya Karunasena
     *
     */
    @Target(value = ElementType.METHOD)
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    public @interface ParameterType {
        /**
         * Parameter index as per {@link PreparedStatement} API.
         */
        public int index();

        /**
         * Sql type of the parameter.
         * <p>
         * <i>E.g.</i> {@link Types#VARCHAR}
         */
        public int sqlType();

        /**
         * Whether this type can be null.
         * <p>
         * <b>Default:</b> false
         */
        public boolean nullable() default false;
    }

    /**
     * Sql query.
     */
    public String sql();

    /**
     * Whether this query has a in clause or not.
     * 
     * <p>
     * <b>Default:</b> false
     */
    public boolean hasInClause() default false;

    /**
     * Array of {@link ParameterType} encapsulating the sql type of each query parameter to be passed in the sql query.
     * <p>
     * <b>Default:</b> {}
     */
    public ParameterType[] paramTypes() default {};
}
