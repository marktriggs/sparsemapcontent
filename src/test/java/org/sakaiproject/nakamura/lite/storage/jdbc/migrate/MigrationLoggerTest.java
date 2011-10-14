/**
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.nakamura.lite.storage.jdbc.migrate;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.nakamura.api.lite.PropertyMigrator;
import org.sakaiproject.nakamura.api.lite.StorageClientUtils;
import org.sakaiproject.nakamura.api.lite.content.Content;
import org.sakaiproject.nakamura.lite.BaseMemoryRepository;
import org.sakaiproject.nakamura.lite.SessionImpl;

import java.util.Map;

public class MigrationLoggerTest {

    private SessionImpl session;

    private MigrationLogger migrationLogger;

    private PropertyMigrator migrator = new PropertyMigrator() {
        public boolean migrate(String rowID, Map<String, Object> properties) {
            return false;
        }

        public boolean verify(String rowID, Map<String, Object> beforeProperties, Map<String, Object> afterProperties) {
            return false;
        }

        public Integer getOrder() {
            return null;
        }
    };

    @Before
    public void setUp() throws Exception {
        this.migrationLogger = new MigrationLogger();
        this.migrationLogger.repository = new BaseMemoryRepository().getRepository();
        this.session = (SessionImpl) this.migrationLogger.repository.loginAdministrative();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testLogAndWrite() throws Exception {

        migrationLogger.log(migrator);
        migrationLogger.write();
        Content logContent = session.getContentManager().get(
                StorageClientUtils.newPath(MigrationLogger.LOG_ROOT_PATH, this.migrator.getClass().getName()));
        Assert.assertNotNull(logContent);
        Assert.assertNotNull(logContent.getProperty(MigrationLogger.DATE_READABLE));
        Assert.assertNotNull(logContent.getProperty(MigrationLogger.DATE_MS));
        Assert.assertTrue(migrationLogger.hasMigratorRun(this.migrator));

        // try a second log-and-write cycle to make sure old data stays
        migrationLogger.log(migrator);
        migrationLogger.write();
        Assert.assertTrue(migrationLogger.hasMigratorRun(this.migrator));
    }
}
