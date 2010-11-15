package org.sakaiproject.nakamura.lite.storage;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class StorageClientUtilsTest {

	@Test
	public void testInt() throws UnsupportedEncodingException {
		for (int i = -100000; i < 10000; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toInt(StorageClientUtils.toStore(i)));
		}
		for (int i = Integer.MIN_VALUE; i < Integer.MIN_VALUE+10000; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toInt(StorageClientUtils.toStore(i)));
		}
		for (int i = Integer.MAX_VALUE-10000; i < Integer.MAX_VALUE; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toInt(StorageClientUtils.toStore(i)));
		}
	}
	
	
	@Test
	public void testLong() throws UnsupportedEncodingException {
		for (long i = -100000; i < 10000; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toLong(StorageClientUtils.toStore(i)));
		}
		for (long i = Long.MIN_VALUE; i < Long.MIN_VALUE+10000; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toLong(StorageClientUtils.toStore(i)));
		}
		for (long i = Long.MAX_VALUE-10000; i < Long.MAX_VALUE; i++) {
			Assert.assertEquals(i,
					StorageClientUtils.toLong(StorageClientUtils.toStore(i)));
		}
	}
	
	@Test
	public void testToString() throws UnsupportedEncodingException {
		Assert.assertEquals(null, StorageClientUtils.toString(null));
		Assert.assertEquals("test", StorageClientUtils.toString("test"));
		Assert.assertEquals("test", StorageClientUtils.toString("test".getBytes("UTF8")));
		Assert.assertEquals("100", StorageClientUtils.toString(100));
	}
	
	@Test
	public void testToBytes() {
		Assert.assertEquals(null, StorageClientUtils.toStore(null));
		Assert.assertEquals("test", StorageClientUtils.toStore("test"));
		Assert.assertEquals(Long.toString(100,StorageClientUtils.ENCODING_BASE), StorageClientUtils.toStore((long)100));
		Assert.assertEquals(Integer.toString(100,StorageClientUtils.ENCODING_BASE), StorageClientUtils.toStore((int)100));
		Object o = new Object();
		Assert.assertEquals(String.valueOf(o), StorageClientUtils.toStore(o));
	}

	@Test
	public void testIsRoot() throws UnsupportedEncodingException {
		Assert.assertTrue(StorageClientUtils.isRoot(null));
		Assert.assertTrue(StorageClientUtils.isRoot(""));
		Assert.assertTrue(StorageClientUtils.isRoot("/"));
		Assert.assertFalse(StorageClientUtils.isRoot("/sdfds"));
	}

	@Test
	public void testGetParentObjectPath() {
		Assert.assertEquals("/", StorageClientUtils.getParentObjectPath("/"));
		Assert.assertEquals("/", StorageClientUtils.getParentObjectPath("/test"));
		Assert.assertEquals("/", StorageClientUtils.getParentObjectPath("/test/"));
		Assert.assertEquals("/test", StorageClientUtils.getParentObjectPath("/test/ing"));
		Assert.assertEquals("/test", StorageClientUtils.getParentObjectPath("/test/ing/"));
	}

	@Test
	public void testGetParentObjectName() {
		Assert.assertEquals("/", StorageClientUtils.getObjectName("/"));
		Assert.assertEquals("test", StorageClientUtils.getObjectName("/test"));
		Assert.assertEquals("test", StorageClientUtils.getObjectName("/test/"));
		Assert.assertEquals("ing", StorageClientUtils.getObjectName("/test/ing"));
		Assert.assertEquals("ing", StorageClientUtils.getObjectName("/test/ing/"));
	}

	@Test
	public void testHash() {
		Assert.assertNotNull(StorageClientUtils.secureHash("test"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetFilterMap() {
		Map<String, Object> t = ImmutableMap.of("a",(Object)"b","c","d");
		Map<String, Object> m = StorageClientUtils.getFilterMap(t, ImmutableSet.of("c"));
		Assert.assertEquals(1, m.size());
		Assert.assertEquals("b",m.get("a"));
		Map<String, Object> t2 = ImmutableMap.of("a",(Object)"b","c","d","e",m);
		Map<String, Object> m2 = StorageClientUtils.getFilterMap(t2, ImmutableSet.of("c"));
		Assert.assertEquals(2, m2.size());
		Assert.assertEquals("b",m2.get("a"));
		m = (Map<String, Object>) m2.get("e");
		Assert.assertEquals(1, m.size());
		Assert.assertEquals("b",m.get("a"));
		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testGetFilteredAndEcodedMap() throws UnsupportedEncodingException {
		Map<String, Object> t = ImmutableMap.of("a",(Object)"b","c","d");
		Map<String, Object> m = StorageClientUtils.getFilteredAndEcodedMap(t, ImmutableSet.of("c"));
		Assert.assertEquals(1, m.size());
		Assert.assertEquals("b", m.get("a"));
		Map<String, Object> t2 = ImmutableMap.of("a",(Object)"b","c","d","e",m);
		Map<String, Object> m2 = StorageClientUtils.getFilteredAndEcodedMap(t2, ImmutableSet.of("c"));
		Assert.assertEquals(2, m2.size());
		Assert.assertEquals("b",m2.get("a"));
		m = (Map<String, Object>) m2.get("e");
		Assert.assertEquals(1, m.size());
		Assert.assertEquals("b", m.get("a"));
	}

}