package org.sakaiproject.nakamura.lite.storage.jdbc.migrate;

import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferenceStrategy;
import org.apache.felix.scr.annotations.Service;
import org.sakaiproject.nakamura.api.lite.PropertyMigrator;

import com.google.common.collect.Sets;

@Component(immediate = true, metatype = true)
@Service(value = PropertyMigratorTracker.class)
@Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, name = "propertyMigrator", referenceInterface = PropertyMigrator.class, policy = ReferencePolicy.DYNAMIC, strategy = ReferenceStrategy.EVENT, bind = "bind", unbind = "unbind")
public class PropertyMigratorTrackerService implements PropertyMigratorTracker {

    private Set<PropertyMigrator> propertyMigrators = Sets.newHashSet();

    public PropertyMigrator[] getPropertyMigrators() {
        synchronized (propertyMigrators) {
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

}
