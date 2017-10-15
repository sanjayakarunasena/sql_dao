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

/**
 * Annotation used to declare a mapping of query result's resulting from a sql query to fields of a Java class. The
 * columns should be ordered as per the resulting column order in the corresponding sql query. If the result is in a
 * single column where the value is mapped to a primitive Java type or a wrapper class (including String & Date types),
 * parameter <i>encapsulate</i> should be set to false. In this case the field mapping in the @Column annotation is
 * ignored.
 * 
 * @author Sanjaya Karunasena
 *
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Result {
    /**
     * Annotation used to declare a column name, its type and the corresponding Java class field.
     * 
     * @author Sanjaya Karunasena
     *
     */
    @Target(value = ElementType.METHOD)
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    public @interface Column {
        /**
         * Column name.
         */
        public String name();

        /**
         * Java class field name to assign the value of this column. When encapsulate is false this value is ignored.
         */
        public String field() default "";

        /**
         * Java type to use when retrieving data. If not specify the default mapping of the JDBC driver is used.
         * <p>
         * <i>E.g.</i> String.class
         */
        public Class<?> type() default DefaultType.class;
    }

    /**
     * Specifies whether the values from columns should be encapsulated. If encapsulate is false it assumed that there
     * is only one column in the result set and it doesn't need encapsulation.
     */
    public boolean encapsulate() default true;

    /**
     * Array of columns declared using {@link Column}
     */
    public Column[] columns();
}
