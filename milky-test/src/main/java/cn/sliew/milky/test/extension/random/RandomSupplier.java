package cn.sliew.milky.test.extension.random;

import java.util.Random;

public interface RandomSupplier {

    RandomSupplier DEFAULT = seed -> new Xoroshiro128PlusRandom(seed);

    Random get(long seed);
}