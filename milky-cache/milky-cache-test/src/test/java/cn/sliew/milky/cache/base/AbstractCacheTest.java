package cn.sliew.milky.cache.base;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.model.FullAddress;
import cn.sliew.milky.cache.model.Person;
import cn.sliew.milky.cache.model.PersonInfo;
import cn.sliew.milky.cache.model.Phone;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractCacheTest extends MilkyTestCase {

    protected Cache cache;
    private Person key = new Person();
    private PersonInfo value;

    {
        value = new PersonInfo();
        ArrayList<Phone> phones = new ArrayList<>();
        Phone phone1 = new Phone("86", "0571", "87654321", "001");
        Phone phone2 = new Phone("86", "0571", "87654322", "002");
        phones.add(phone1);
        phones.add(phone2);
        value.setPhones(phones);
        Phone fax = new Phone("86", "0571", "87654321", null);
        value.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "3480", "wensanlu", "wensanlu","wensanluwensanluwensanluwensanluwensanluwensanlu","315000");
        value.setFullAddress(addr);
        value.setMobileNo("13584652131");
        value.setName("superman");
        value.setMale(true);
        value.setDepartment("b2b");
        value.setJobTitle("qa");
        value.setHomepageUrl("www.capcom.com");
    }

    @AfterEach
    private void afterAll() {
        cache.clear();
    }

    @Test
    public void testGet() {
        cache.put(key, value);
        assertEquals(value, cache.get(key));

        assertNull(cache.get("123"));
    }

    @Test
    public void testNullKey() {
        if (cache.supportNullKey()) {
            cache.put(null, value);
            assertEquals(value, cache.get(null));
        }
    }

    @Test
    public void testNullValue() {
        if (cache.supportNullValue()) {
            cache.put(key, null);
            assertNull(cache.get(key));
        }
    }

    @Test
    public void testGetWithLoader() {
        Object result = cache.computeIfAbsent(key, (key1) -> value, Duration.ofMillis(1000L));
        assertEquals(value, result);
        assertEquals(value, cache.get(key));
    }

    @Test
    public void testRemove() {
        cache.put(key, value);
        assertNotNull(cache.get(key));
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testRemoveAll() {
        cache.put(key, value);
        cache.put("123", "456");
        assertNotNull(cache.get(key));
        assertNotNull(cache.get("123"));
        cache.removeAll(Arrays.asList(key, "123"));
        assertNull(cache.get(key));
        assertNull(cache.get("123"));
    }

    @Test
    public void testSize() {
        cache.put(key, value);
        cache.put("123", "456");
        assertEquals(2, cache.size());
    }

    @Test
    public void testKeyIterator() {
        cache.put(key, value);
        cache.put("123", "456");
        Iterator iterator = cache.keyIterator();
        AtomicInteger i = new AtomicInteger();
        iterator.forEachRemaining((obj) -> i.incrementAndGet());
        assertEquals(2, i.get());
    }
}
