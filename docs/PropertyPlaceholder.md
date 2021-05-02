# PropertyPlaceholder

在很多的配置库中，支持配置的引用，如下：

```properties
my.prop1 = value1
my.prop2 = ${my.prop1}
my.prop3.${my.prop2} = value3
```

使用过 `ORM` 框架 [`mybatis`](https://mybatis.org/mybatis-3/sqlmap-xml.html#Parameters) 的也一定对下面的代码非常熟悉：

```xml
<insert id="insertUser" parameterType="User">
  insert into users (id, username, password)
  values (#{id}, #{username}, #{password})
</insert>
```

而在官网文档中的 `String Substitution` 部分对 `#{}` 和 `${}` 有如下描述：

> By default, using the `#{}` syntax will cause MyBatis to generate `PreparedStatement` properties and set the values safely against the `PreparedStatement` parameters (e.g. ?). While this is safer, faster and almost always preferred, sometimes you just want to directly inject an unmodified string into the SQL Statement. For example, for ORDER BY, you might use something like this:
>
> ```xml
> ORDER BY ${columnName}
> ```

这些功能都用到一种字符串解析替换技术：占位符处理。

`PropertyPlaceholder` 就是一个小而精巧的字符串占位符处理组件。

## 占位符格式

就像高楼在设计房间号的时候都会把楼层编码进去，如 `1201` 即为 `12 楼 01 号房间`。这种命名方式在社会收到广泛流传和认可，形成一种普遍的基本生活认知，而高楼也会普遍遵从这种房间命名方式。

占位符也存在广泛使用的格式，即 `${}`。在众多使用占位符的库都会使用 `${}` 作为占位符标识，在 `runtime` 期间程序会自动把 `${}` 替换成相应的属性值。`mybatis` 框架虽然使用在 `mapper` 文件中  `#{}` 用的比较多，也只是把 `#{}` 解析成 `?` ，而对于 `${}` 采用直接替换的方式。

为了支持多种占位符格式，`PropertyPlaceholder` 在使用的时候需要指定占位符的格式 `placeholderPrefix` 和 `placeholderSuffix`。对于 `${}`格式，`${` 即为 `placeholderPrefix`，`}` 即为 `placeholderSuffix`。

## 属性值解析

对于占位符 `${property}` 引用的属性该替换为对应的属性值呢？对于配置框架中的引用，属性值可以限定在同一个配置文件中，但是对于像 `mybatis` 这种强依赖配置、重度使用占位符的框架，待解析的属性占位符依据不同的配置地点，有不同的上下文范围。为了方便解析，提供专门的属性值解析接口。

```java
interface PlaceholderResolver {

    Optional<String> resolvePlaceholder(String placeholderName);
}
```

## 嵌套引用

支持嵌套引用。

```properties
prop1 = ${prop2}
prop2 = ${prop3}
prop3 = value3

# prop1 = prop2 = prop3 = value
```

## 解析失败处理

```properties
prop1 = ${unknownProperty}
```

当占位符引用了不存在的属性值的时候该如何处理？

组件提供了两级的控制。

可以在创建 `PropertyPlaceholder` 提供参数 `ignoreUnresolvablePlaceholders` 控制是否忽略处理替换失败的占位符。

也可以在 `PlaceholderResolver` 处理解析失败的占位符。

```java
interface PlaceholderResolver {

    boolean shouldIgnoreMissing(String placeholderName);

    boolean shouldRemoveMissingPlaceholder(String placeholderName);
}
```

## 占位符默认值

对于解析失败的场景可以为开发者提供另一种解决思路：提供默认值。

格式如下：

```properties
prop1 = ${prop2:defaultValue}
```

## 转义支持

不可避免地，属性值如果也存在 `${}` 占位符标识，但是又不能作为占位符进行解析该如何处理？

`PropertyPlaceholder` 提供了转义支持。

```properties
prop1 = \\${value\\}
```