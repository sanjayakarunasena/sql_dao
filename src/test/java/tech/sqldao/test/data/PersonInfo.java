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
package tech.sqldao.test.data;

/**
 * @author Sanjaya Karunasena
 *
 */
public class PersonInfo {
    private int id;
    private String name;
    private int age;
    private String email;
    private String city;

    /**
     * Default constructor.
     */
    public PersonInfo() {
    }

    /**
     * Convenient constructor.
     * 
     * @param id
     * @param name
     * @param age
     * @param email
     * @param city
     */
    public PersonInfo(int id, String name, int age, String email, String city) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.city = city;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Print PersonInfo to the console.
     * 
     * @param pInfo
     */
    public static void print(PersonInfo pInfo) {
        System.out.print(pInfo.getId() + "\t");
        System.out.print(pInfo.getName() + "\t");
        System.out.print(pInfo.getAge() + "\t");
        System.out.print(pInfo.getEmail() + "\t");
        System.out.println(pInfo.getCity());
    }
}
