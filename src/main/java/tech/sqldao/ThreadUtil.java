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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sanjaya Karunasena
 *
 */
public class ThreadUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadUtil.class);

    /**
     * Delay the execution of this thread by the specified period. If this thread is interrupted during this period the
     * execution is restarted.
     * 
     * @param period
     *            Delay period in milliseconds.
     */
    public static void delay(long period) {
        try {
            Thread.sleep(period);
        } catch (InterruptedException ie) {
            LOG.warn("This thread is interupted while sleeping and execution restarted.");
        }
    }

}
