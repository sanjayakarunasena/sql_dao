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

import tech.sqldao.AbstractDAO;

/**
 * Annotation used to declare DAO methods in a class with {@link Query} and / or {@link Result} annotation. This is used
 * to figure out the relevant annotations of a method during runtime. {@link DaoMethod} declarations order should match
 * the order of the DAO class actual method declaration with {@link Query} annotation since Java annotation only allows
 * primitive data types and single dimensional arrays. The index which need to pass in to the corresponding
 * {@link AbstractDAO} methods must be the zero based index of the list of {@link DaoMethod}s declarations.
 * 
 * @author Sanjaya Karunasena
 *
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DaoMethods {
    /**
     * Annotation used to declare a DAO method which uses the {@link Query} annotation.
     * 
     * @author Sanjaya Karunasena
     *
     */
    @Target(value = ElementType.TYPE)
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    public @interface DaoMethod {
        /**
         * Zero based index of the DAO method.
         */
        public int index();

        /**
         * DAO Method name.
         */
        public String name();

        /**
         * DAO Method formal parameter types.
         * <p>
         * <b>Default:</b> {}
         */
        public Class<?>[] paramTypes() default {};
    }

    /**
     * List of {@link DaoMethod}s which uses annotation {@link Query}.
     */
    public DaoMethod[] methods();
}
