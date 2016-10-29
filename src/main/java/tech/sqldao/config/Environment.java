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
package tech.sqldao.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sanjaya Karunasena
 *
 */
public class Environment {
    public final static String HOME_DIRECTORY = "APPLICATION_HOME";

    private String homeDir;

    /**
     * Get the environment variables relevant to the server and return an instance of the Environment.
     * 
     * @return The Environment
     */
    public static synchronized Environment getEnvironment() {
        Logger logger = LoggerFactory.getLogger(Environment.class);
        Environment env = new Environment();
        String homeDir = System.getenv(Environment.HOME_DIRECTORY);
        if (homeDir != null) {
            env.setHomeDir(homeDir);
            logger.debug(Environment.HOME_DIRECTORY + " is " + env.getHomeDir());
        } else {
            env.setHomeDir(System.getProperty("user.dir"));
            logger.warn(Environment.HOME_DIRECTORY + " is not set, using " + env.getHomeDir());
        }

        return env;
    }

    /**
     * @return the homeDir
     */
    public String getHomeDir() {
        return homeDir;
    }

    /**
     * @param homeDir
     *            the homeDir to set
     */
    private void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }
}
