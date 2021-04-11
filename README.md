# 组件列表

## TODO

* 使用 `Supplier` 等取代代码中的部分功能。类似 `checkNotNull(Object obj, Object message)`  中如果 `obj` 不为 `null` 的时候是不需要计算检测失败的提示信息的，使用 `lambda` 可以实现检测失败的时候再去创建提示信息对象，实现延迟创建效果。
* 使用 `Optional` 取代可能返回结果为 `null` 的接口。使用 `@Nullable` 效果不如使用 `Optional` 。
* 线程池组件。支持多种形式的线程池，添加新的线程池拒绝策略，支持动态调整线程池参数。

## 发布命令
mvn clean deploy -Dmaven.deploy.skip=false -Poss-release -projects milky

* 单位组件。如时间、byte、内存等。
* 遍历组件。树形遍历，图形遍历



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



## 单位组件

### 时间单位



### byte单位



## 遍历组件

`Visitor`和`Filter`。参考dolphinscheduler或junit5的`NodeTreeWalker`。



##占位符组件

参考mybatis的`#{}`和`${}`的占位符解析。

com.carrotsearch.randomizedtesting.rules.RamUsageEstimator

