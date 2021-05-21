# LifeCycle Component

在构建大型项目的时候，为了解决复杂性，往往会通过组件、模块对功能进行划分。`Milky` 致力于成为项目开发中的组件库，提供了 `LifeCycle` 组件解决组件使用过程中的生命周期问题。

## 生命周期状态

`LifeCycle` 将生命周期定义为三个阶段：初始化，启动和停止。`LifeCycle` 功能简洁而强大，如果需要有更复杂的阶段，可以参考 `LifeCycle` 的代码扩充新的阶段，或者使用更复杂的方式，如状态机、状态模式等。

`LifeCycle` 提供多种方法查询组件状态。

```java
State getState();
boolean isStarted();
boolean isStopped();
```

## 生命周期事件

与状态对应的是，`LifeCycle` 提供了初始化、启动和停止三个事件，调用后可以对组件进行初始化、启动和停止。`LifeCycle` 对生命周期的状态转换做了一定限制，如可以从初始化运行到启动阶段，从启动阶段运行到停止阶段，也可以从停止阶段运行到启动阶段，并提供了相应地方法判断是否可以支持相应事件触发。

```java
LifeCycleResult initialize();
LifeCycleSupportResult supportInitialize();
LifeCycleResult start();
LifeCycleSupportResult supportStart();
LifeCycleResult stop();
LifeCycleSupportResult supportStop();
LifeCycleResult stop(Duration timeout);
```

优秀的系统一定是可观测的系统，`LifeCycle` 会返回事件结果、失败原因等信息辅助排查问题。

```java
class LifeCycleResult {

    private final boolean status;
    private final Throwable throwable;

    private LifeCycleResult(boolean status, Throwable throwable) {
        this.status = status;
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return status;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static LifeCycleResult success() {
        return new LifeCycleResult(true, null);
    }

    public static LifeCycleResult failure(Throwable throwable) {
        return new LifeCycleResult(false, throwable);
    }
}

class LifeCycleSupportResult {

    private final boolean support;
    private final String reason;

    private LifeCycleSupportResult(boolean support, String reason) {
        this.support = support;
        this.reason = reason;
    }

    public boolean support() {
        return support;
    }

    public String reason() {
        return reason;
    }

    public static LifeCycleSupportResult support(String reason) {
        return new LifeCycleSupportResult(true, reason);
    }

    public static LifeCycleSupportResult unsupport(String reason) {
        return new LifeCycleSupportResult(false, reason);
    }
}
```

## 事件监听器

如果对于将所有的功能塞填到 `#start()`、`#stop()` 等方法中不满意的话，可以适当考虑使用 `LifeCycleListener`，但是这是个监听器，而不是拦截器或者处理器，对于那些功能适合使用 `LifeCycleListener`，需要根据实际需要细心斟酌。

```java
public interface LifeCycleListener<T extends LifeCycle> {

    /**
     * Before component initialize.
     */
    default void beforeInitialize(T component) {

    }

    /**
     * After component initialize.
     */
    default void afterInitialize(T component) {

    }

    /**
     * Before component start.
     */
    default void beforeStart(T component) {

    }

    /**
     * After component start.
     */
    default void afterStart(T component) {

    }

    /**
     * Before component stop.
     */
    default void beforeStop(T component) {

    }

    /**
     * After component stop.
     */
    default void afterStop(T component) {

    }
}
```

