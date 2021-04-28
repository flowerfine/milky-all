# Context

很多时候都需要实现串联上下文，比较典型的如分布式链路追踪，需要在调用链路上传输链路追踪id用以关联链路。而很多分布式框架如 `dubbo`，消息队列等也在数据传输中增加了上下文信息透传的能力，如 `dubbo` 的 `RpcContext`支持的 `attachments`，`rocketmq` 支持的 `headers`。

但是很多时候在一个比较小的范围内使用上下文`Context`对象串联。一般要将数据在多个方法中进行传递有两种方式：ThreadLocal与Context入参。使用ThreadLocal，每个方法都可以对当前ThreadLocal进行赋值和取值，因为ThreadLocal的“隐式调用”作用，可以让context只在需要的地方出现，可以起到简化代码的效果。缺点是应用ThreadLocal的地方很像魔法，开发者理解应用ThreadLocal的代码意图可能会存在不便，难以追踪ThreadLocal中数据的变更。而使用Context作为入参在方法间进行传递，可以有效的减少“不可知”的问题。

而context对象作为数据传输的载体，常见的是一个类Map的实现或者Stack实现。在应用map的时候一般是用来作为数据传递，使用key/value比较方便。在使用stack的场景有调用栈的处理，如进行一些profile或者explain。

