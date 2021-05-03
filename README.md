# 组件列表

## TODO

* 使用 `Supplier` 等取代代码中的部分功能。类似 `checkNotNull(Object obj, Object message)`  中如果 `obj` 不为 `null` 的时候是不需要计算检测失败的提示信息的，使用 `lambda` 可以实现检测失败的时候再去创建提示信息对象，实现延迟创建效果。
* 使用 `Optional` 取代可能返回结果为 `null` 的接口。使用 `@Nullable` 效果不如使用 `Optional` 。
* 线程池组件。支持多种形式的线程池，添加新的线程池拒绝策略，支持动态调整线程池参数。

## 发布命令
mvn clean deploy -Poss-release -N versions:update-child-modules

* 单位组件。如时间、byte、内存等。
* 遍历组件。树形遍历，图形遍历
* 异常组件。[异常采集器](#任务异常采集器)，异常重抛，异常堆栈格式化。



## 生命周期

优秀的java框架都提供了生命周期接口，管理框架内组件的生命周期，确保组件正确地初始化、创建和结束。生命周期组件提供接口 `LifiCycle`，它有6个状态，分别表示：

* `INITIALIZING`。正在初始化，还没有完成初始化。
* `INITIALIZED`。初始化完毕，还没有开始。
* `STARTING`。正在开始，还没有完成开始。
* `STARTED`。已经开始，可以正常使用。
* `STOPPING`。正在结束，还没有完成结束。
* `STOPPED`。已经结束，可以正常关闭。

## 线程池组件



## ThreadLocal组件



## ThreadContext组件



## 任务异常采集器

在开发中，异常不可避免，异常处理就需要细心处理。

`milky` 提供了 [`ThrowableCollector`](https://github.com/kalencaya/milky-all/blob/master/docs/ThrowableCollector.md) 组件让异常捕获处理如丝般顺滑。

## 异常重抛

客户端在调用API时，不得不处理讨厌的受检查异常，如下：

```java
try {
    // do something may throw exception.
} catch (Exception e) {
    throw e; // compile failure.
}
```

很多时候，开发者并不想在当前位置对异常进行处理，而是将异常处理的工作委托给更上层的代码，但是又不想受API影响，在方法上跟着声明受检查的异常（这也是 `java` 中受检查异常被吐槽的点之一，它破坏了封装）。这个时候只想简单地抛出异常，可以使用 `Rethrower` 组件实现这个功能。

## 异常堆栈格式化

在 `java` 开发环境中，日志系统如 `log4j2`、`logback` 等框架可以轻松地实现输出日志堆栈信息到日志文件中，供问题排查。

但是输出到日志文件中的异常信息只能供有权限能够访问到日志数据的开发者使用。很多时候开发者想提高用户自助问题排查的能力，比如为用户提供 `debug` 信息，将异常信息作为结果的一部分返回给用户。

`ThrowableTraceFormatter` 提供了将 `Throwable` 堆栈格式化为字符串的能力，方便开发者处理异常信息。

## 版本管理

`SemVersion`遵从语义化版本管理。链接：https://semver.org/。

## 单位组件

### 时间单位



### byte单位



## 遍历组件

`Visitor`和`Filter`。参考dolphinscheduler或junit5的`NodeTreeWalker`。

## 占位符组件

作为一种常见的功能，支持属性值的引用是一种简单而作用重大的功能。`milky` 提供了 [`PropertyPlaceholder`](https://github.com/kalencaya/milky-all/blob/master/docs/PropertyPlaceholder.md) 组件提供开箱即用的占位符功能。

* 指定占位符格式。开发者可以自由使用 `${}`、`{}` 等格式，只需要指定前缀和后缀即可。
* 循环引用。占位符支持嵌套，可以实现 `a` 引用 `b`，`b` 引用 `c`，从而 `a = b = c = c_value`。
* 失败处理。可以通过参数与继承接口控制解析失败的处理。
* 默认值支持。失败处理的另一种思路是提供默认值支持，让使用者参与进失败处理。
* 转义支持。如果属性值中也存在 `${}`，而又不想触发循环引用的功能，可以使用` \\` 对 `${}` 进行转义。 

## 对象size

com.carrotsearch.randomizedtesting.rules.RamUsageEstimator

## 工具类

 