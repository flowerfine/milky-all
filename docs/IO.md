# IO

milky-io 致力于提供高效地底层数据序列化操作，可用于数据传输、序列化、数据存储等场景。

可变类型编码+zigzag编码优化性能

是否为null的优化



参考了 `dubbo` 的 `ChannelBuffer` ，`flink` 的 `DataInputView` 和 `DataOutputView`，`Kryo` 的 `Input` 和 `Output` 以及 `elasticsearch` 的 `StreamInput` 和 `StreamOutput` 设计。其中 `dubbo` 和 `flink` 的设计来源于 `jdk` 的 `DataInput` 和 `DataOutput`，而 `kryo` 和 `elasticsearch` 的设计来源于 `jdk` 的 `InputStream` 和 `OutputStream`。

因为 `DataInput` 和 `DataOutput` 定义了基本类型，String 类型和 bytes 数组的读写，可以省略一部分 API 定义，所以这里也是继承了 `DataInput` 和 `DataOutput` 接口实现。

基础类型



集合类型，`Map` 类型和数组



特殊类型。如日期相关，String，自定义接口。



`InputStream` 和 `OutputStream` ，`DataInput` 和 `DataOutput`，`ByteBuffer` 和

