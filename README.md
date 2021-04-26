# 组件列表

## TODO

* 使用 `Supplier` 等取代代码中的部分功能。类似 `checkNotNull(Object obj, Object message)`  中如果 `obj` 不为 `null` 的时候是不需要计算检测失败的提示信息的，使用 `lambda` 可以实现检测失败的时候再去创建提示信息对象，实现延迟创建效果。
* 使用 `Optional` 取代可能返回结果为 `null` 的接口。使用 `@Nullable` 效果不如使用 `Optional` 。
* 线程池组件。支持多种形式的线程池，添加新的线程池拒绝策略，支持动态调整线程池参数。

## 发布命令
mvn clean deploy -Poss-release -N versions:update-child-modules

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



## 任务异常采集器

在开发中，异常不可避免，异常处理就需要细心处理。

回头看下代码整洁之道里面对异常的处理。异常的处理外移，内部不要吞异常。整洁代码之道中建议开发中先写`try-catch-finally`。异常的处理与函数式编程结合。在函数式编程中如果添加了大量的`try-catch-finally`，lambda也直接失去了函数表达式的简洁性，不再是一个**小函数**，开始变得复杂。

另外主动抛出的异常也会破坏函数的纯洁性，函数式编程的优势便是结果的一致性，异常会破坏结果的类型一致，返回类型会包括异常类型。

因此需要一种函数式的异常处理组件来维护函数式编程的纯洁性。

https://blog.knoldus.com/functional-error-handling-in-scala/

scala提供了`Option`表示有结果和null值，`Try` 结果有`Success`和`Failure`，`Either`。其中Option只能返回结果值或null值，异常的情况下只能有null来返回，但是有些业务null值可能也是一个正常的业务返回结果。Try会返回异常，但是仍然要去处理异常，以函数式的方式添加异常处理的代码。Either做到了捕获异常对象，作为返回结果的一部分。

scala语言直接提供了语言级的支持，而java的函数式编程库vavr提供了类似的实现。而这个小组件简单地提供了异常的捕获功能。

https://docs.vavr.io/#_try

## 版本管理

`SemVersion`遵从语义化版本管理。链接：https://semver.org/。

## 单位组件

### 时间单位



### byte单位



## 遍历组件

`Visitor`和`Filter`。参考dolphinscheduler或junit5的`NodeTreeWalker`。

## 占位符组件

参考mybatis的`#{}`和`${}`的占位符解析。

spring的placeholder。

es的PropertyPlaceholder

## 对象size

com.carrotsearch.randomizedtesting.rules.RamUsageEstimator

## 工具类

 