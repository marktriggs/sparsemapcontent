package uk.co.tfd.sm.proxy;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolvableResource implements Resource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResolvableResource.class);
	private String value;
	private String label;
	private Resolver resolver;
	private Map<String, Object> resolverConfig;

	public ResolvableResource(String value, Resolver resolver, Map<String, Object> resolverConfig) {
		this.value = value;
		this.resolver = resolver;
		this.resolverConfig = resolverConfig;
		LOGGER.info("Resolvable Resource Created for {} ",value);
	}
	
	
	public synchronized String getLabel() throws IOException {
		if ( label == null && resolver != null ) {
			Map<String, Object> map = resolver.get(value, resolverConfig);
			label = (String) map.get("rdfs_label");
			LOGGER.info("Got label for {} as {}",value, label);
		}
		return label;
	}
	public String getKey() {
		return value;
	}

	public boolean hasLabelAndKey() {
		LOGGER.info("Checking Label and Key on {} ",value);
		return true;
	}

	public boolean isReference() {
		LOGGER.info("Checking Reference and Key on {} ",value);
		return true;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
