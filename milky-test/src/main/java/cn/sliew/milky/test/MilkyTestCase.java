package cn.sliew.milky.test;

import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import org.junit.runner.RunWith;

@RunWith(RandomizedRunner.class)
public class MilkyTestCase {

    public static String randomAlphaOfLengthBetween(int minCodeUnits, int maxCodeUnits) {
        return RandomizedTest.randomAsciiOfLengthBetween(minCodeUnits, maxCodeUnits);
    }

    public static String randomAlphaOfLength(int codeUnits) {
        return RandomizedTest.randomAsciiOfLength(codeUnits);
    }
}
