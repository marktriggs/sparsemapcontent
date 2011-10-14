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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferenceStrategy;
import org.apache.felix.scr.annotations.Service;
import org.sakaiproject.nakamura.api.lite.PropertyMigrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The PropertyMigratorTracker service tracks unique PropertyMigrators
 * efficiently and stores them should an operator want to activate the
 * MigrateContentComponent and perform a migration. The reason this Component is
 * here is so that its live in the system as early as possible and it can track
 * any PropertyMigrators that have been provided by other bundles. If it were
 * not active it would not be able to track, and there is a danger, depending on
 * which OSGi container is being used, that some PropertyMigrators might not get
 * registered.
 *
 * @author ieb
 *
 */
@Component(immediate = true, metatype = true)
@Service(value = PropertyMigratorTracker.class)
@Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, name = "propertyMigrator", referenceInterface = PropertyMigrator.class, policy = ReferencePolicy.DYNAMIC, strategy = ReferenceStrategy.EVENT, bind = "bind", unbind = "unbind")
public class PropertyMigratorTrackerService implements PropertyMigratorTracker {

    private static final PropertyMigratorComparator COMPARATOR = new PropertyMigratorComparator();

    private final List<PropertyMigrator> propertyMigrators = new ArrayList<PropertyMigrator>();

    public PropertyMigrator[] getPropertyMigrators() {
        synchronized (propertyMigrators) {
            Collections.sort(this.propertyMigrators, COMPARATOR);
            return propertyMigrators.toArray(new PropertyMigrator[propertyMigrators.size()]);
        }
    }

    public void bind(PropertyMigrator pm) {
        synchronized (propertyMigrators) {
            propertyMigrators.add(pm);
        }
    }

    public void unbind(PropertyMigrator pm) {
        synchronized (propertyMigrators) {
            propertyMigrators.remove(pm);
        }
    }

    private static class PropertyMigratorComparator implements Comparator<PropertyMigrator> {
        public int compare(PropertyMigrator a, PropertyMigrator b) {
            if (a.getOrder() == null) {
                return 1;
            }
            if (b.getOrder() == null) {
                return -1;
            }
            return a.getOrder().compareTo(b.getOrder());
        }
    }
}
