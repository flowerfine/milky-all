package cn.sliew.milky.common.concurrent;

import java.util.concurrent.Executor;

public interface CapacityExecutor extends Executor {

    boolean hasCapacity();

    Integer availableCapacity();
}
