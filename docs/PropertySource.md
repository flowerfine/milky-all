# PropertySource

配置和编程没有特别明显的界限，简单的问题可以用配置解决，复杂的则需要编程参与，可配置就一定可编程！

在微服务中通过配置中心将配置集中管理，动态推送配置到应用中实现运行时应用配置变更，避免变更配置而重新编译打包部署应用。

配置中心通过适配 `spring` 的配置功能实现与 `spring` 的打通。那么 `spring` 的配置功能是如何实现的呢？

在 `spring` 的世界中配置的来源有很多种，比如命令行参数 `-Dkey=value`，配置文件 `application.properties`，以及系统属性 `System.getProperties()` 和环境变量 `System.getenv()`，以及像 `apollo`、`nacos` 这种配置中心。

在不同的配置源中提供配置方式不同，应用使用配置前需要经过转换将配置源中的数据进行处理。

配置值最常见的类型为字符串，但是像数字类型、布尔类型也非常常见。在面向对象的世界中，也需要将多个配置能够自动装配成一个配置对象，避免因为配置而产生的过程式代码。

因为支持多种配置源，就要考虑配置在不同配置源中重复的问题，需要为配置源指定优先级，当发生配置重复时优先级高的配置源中的配置生效。



配置管理。命名空间，分组

配置过滤和加密

默认值，占位符

## `PropertySource`

在 `spring` 中 `PropertySource` 是一系列 `name/value` 属性对的抽象，表示一个配置源。

```java
public abstract class PropertySource<T> {

	protected final String name;

	protected final T source;

	public PropertySource(String name, T source) {
		Assert.hasText(name, "Property source name must contain at least one character");
		Assert.notNull(source, "Property source must not be null");
		this.name = name;
		this.source = source;
	}

	public String getName() {
		return this.name;
	}

	public T getSource() {
		return this.source;
	}

	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	@Nullable
	public abstract Object getProperty(String name);

}
```

* name。配置源名称。
* source。原始配置对象。

`PropertySources` 用于遍历搜索配置源。

```java
public interface PropertySources extends Iterable<PropertySource<?>> {

	default Stream<PropertySource<?>> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	boolean contains(String name);

	@Nullable
	PropertySource<?> get(String name);

}
```

## `PropertyResolver`

解析 `PropertySource` 的配置，用于程序获取配置源的配置。

```java
public interface PropertyResolver {

	boolean containsProperty(String key);

	@Nullable
	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	@Nullable
	<T> T getProperty(String key, Class<T> targetType);

	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	String getRequiredProperty(String key) throws IllegalStateException;

	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	String resolvePlaceholders(String text);

	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
```

* 默认值。当配置不存在时，使用默认值。
* 类型转换。将配置值转换成指定类型。
* 必需属性。当配置不存在时，抛出异常。
* 占位符。提供占位符解析功能。

## `EnumerablePropertySource`

`EnumerablePropertySource` 提供了获取属性源所有属性名的功能。

```java
public abstract class EnumerablePropertySource<T> extends PropertySource<T> {

	public EnumerablePropertySource(String name, T source) {
		super(name, source);
	}

	@Override
	public boolean containsProperty(String name) {
		return ObjectUtils.containsElement(getPropertyNames(), name);
	}

	public abstract String[] getPropertyNames();

}
```

## `MapPropertySource`

`MapPropertySource` 提供了 `PropertySource` 的默认实现，基于 `Map<String, Object>`。

```java
public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {

	public MapPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	@Override
	@Nullable
	public Object getProperty(String name) {
		return this.source.get(name);
	}

	@Override
	public boolean containsProperty(String name) {
		return this.source.containsKey(name);
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.keySet());
	}

}
```

## `PropertiesPropertySource`

`PropertiesPropertySource` 在 `MapPropertySource` 的基础上提供了 `java.util.Properties` 的实现。

```java
public class PropertiesPropertySource extends MapPropertySource {

	@SuppressWarnings({"rawtypes", "unchecked"})
	public PropertiesPropertySource(String name, Properties source) {
		super(name, (Map) source);
	}

	protected PropertiesPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	@Override
	public String[] getPropertyNames() {
		synchronized (this.source) {
			return super.getPropertyNames();
		}
	}

}
```

## `SystemEnvironmentPropertySource`

`SystemEnvironmentPropertySource` 在 `MapPropertySource` 的基础上提供了获取环境变量配置的功能。

```java
public class SystemEnvironmentPropertySource extends MapPropertySource {

	public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	@Override
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	@Override
	@Nullable
	public Object getProperty(String name) {
		String actualName = resolvePropertyName(name);
		if (logger.isDebugEnabled() && !name.equals(actualName)) {
			logger.debug("PropertySource '" + getName() + "' does not contain property '" + name +
					"', but found equivalent '" + actualName + "'");
		}
		return super.getProperty(actualName);
	}

	/**
	 * Check to see if this property source contains a property with the given name, or
	 * any underscore / uppercase variation thereof. Return the resolved name if one is
	 * found or otherwise the original name. Never returns {@code null}.
	 */
	protected final String resolvePropertyName(String name) {
		Assert.notNull(name, "Property name must not be null");
		String resolvedName = checkPropertyName(name);
		if (resolvedName != null) {
			return resolvedName;
		}
		String uppercasedName = name.toUpperCase();
		if (!name.equals(uppercasedName)) {
			resolvedName = checkPropertyName(uppercasedName);
			if (resolvedName != null) {
				return resolvedName;
			}
		}
		return name;
	}

	@Nullable
	private String checkPropertyName(String name) {
		// Check name as-is
		if (containsKey(name)) {
			return name;
		}
		// Check name with just dots replaced
		String noDotName = name.replace('.', '_');
		if (!name.equals(noDotName) && containsKey(noDotName)) {
			return noDotName;
		}
		// Check name with just hyphens replaced
		String noHyphenName = name.replace('-', '_');
		if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
			return noHyphenName;
		}
		// Check name with dots and hyphens replaced
		String noDotNoHyphenName = noDotName.replace('-', '_');
		if (!noDotName.equals(noDotNoHyphenName) && containsKey(noDotNoHyphenName)) {
			return noDotNoHyphenName;
		}
		// Give up
		return null;
	}

	private boolean containsKey(String name) {
		return (isSecurityManagerPresent() ? this.source.keySet().contains(name) : this.source.containsKey(name));
	}

	protected boolean isSecurityManagerPresent() {
		return (System.getSecurityManager() != null);
	}

}
```

对于配置项，`SystemEnvironmentPropertySource` 提供了多种形式的兼容。

比如将 `.` 转换成 `_`，将 `-` 转换成 `_`，将配置项转换成全部大写，在 `SystemEnvironmentPropertySource#checkPropertyName(String)` 方法中会通过多种方式尝试获取配置项，以兼容环境变量。

## `CommandLinePropertySource`

jdk 的 main 方法支持字符串数组形式的参数，这些参数可以用于程序运行。

但是很多时候我们会在程序中声明一些属性：

```java
@Value("${foo.bar}")
private String name;
```

`spring` 也同样支持在命令行中传入键值对形式的配置参数：

```java
-Dfoo.bar=name
```

因此在启动 `spring` 项目的时候，启动命令如下：

```shell
java -jar xxx.jar -Dfoo.bar=name /path/to/log /path/to/data
```

因此命令行配置参数有两种形式，一种是 main 函数支持的字符串数组，称之为 `nonOptionArgs`，另一种是键值对的形式，称之为 `optionArgs`。

```java
public abstract class CommandLinePropertySource<T> extends EnumerablePropertySource<T> {

	/** The default name given to {@link CommandLinePropertySource} instances: {@value}. */
	public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";

	/** The default name of the property representing non-option arguments: {@value}. */
	public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";


	private String nonOptionArgsPropertyName = DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;


	/**
	 * Create a new {@code CommandLinePropertySource} having the default name
	 * {@value #COMMAND_LINE_PROPERTY_SOURCE_NAME} and backed by the given source object.
	 */
	public CommandLinePropertySource(T source) {
		super(COMMAND_LINE_PROPERTY_SOURCE_NAME, source);
	}

	/**
	 * Create a new {@link CommandLinePropertySource} having the given name
	 * and backed by the given source object.
	 */
	public CommandLinePropertySource(String name, T source) {
		super(name, source);
	}


	/**
	 * Specify the name of the special "non-option arguments" property.
	 * The default is {@value #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME}.
	 */
	public void setNonOptionArgsPropertyName(String nonOptionArgsPropertyName) {
		this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
	}

	@Override
	public final boolean containsProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			return !this.getNonOptionArgs().isEmpty();
		}
		return this.containsOption(name);
	}

	@Override
	@Nullable
	public final String getProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			Collection<String> nonOptionArguments = this.getNonOptionArgs();
			if (nonOptionArguments.isEmpty()) {
				return null;
			}
			else {
				return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
			}
		}
		Collection<String> optionValues = this.getOptionValues(name);
		if (optionValues == null) {
			return null;
		}
		else {
			return StringUtils.collectionToCommaDelimitedString(optionValues);
		}
	}


	protected abstract boolean containsOption(String name);

	@Nullable
	protected abstract List<String> getOptionValues(String name);

	protected abstract List<String> getNonOptionArgs();

}
```

### `SimpleCommandLinePropertySource`

main 函数字符串数组形式的参数实现类如下。

```java
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {

	public SimpleCommandLinePropertySource(String... args) {
		super(new SimpleCommandLineArgsParser().parse(args));
	}

	public SimpleCommandLinePropertySource(String name, String[] args) {
		super(name, new SimpleCommandLineArgsParser().parse(args));
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.getOptionNames());
	}

	@Override
	protected boolean containsOption(String name) {
		return this.source.containsOption(name);
	}

	@Override
	@Nullable
	protected List<String> getOptionValues(String name) {
		return this.source.getOptionValues(name);
	}

	@Override
	protected List<String> getNonOptionArgs() {
		return this.source.getNonOptionArgs();
	}

}
```

### `JOptCommandLinePropertySource`

对于键值对的解析是基于 [jopt-simple](https://github.com/jopt-simple/jopt-simple) 实现的。

```java
public class JOptCommandLinePropertySource extends CommandLinePropertySource<OptionSet> {

	public JOptCommandLinePropertySource(OptionSet options) {
		super(options);
	}

	public JOptCommandLinePropertySource(String name, OptionSet options) {
		super(name, options);
	}


	@Override
	protected boolean containsOption(String name) {
		return this.source.has(name);
	}

	@Override
	public String[] getPropertyNames() {
		List<String> names = new ArrayList<>();
		for (OptionSpec<?> spec : this.source.specs()) {
			String lastOption = CollectionUtils.lastElement(spec.options());
			if (lastOption != null) {
				// Only the longest name is used for enumerating
				names.add(lastOption);
			}
		}
		return StringUtils.toStringArray(names);
	}

	@Override
	@Nullable
	public List<String> getOptionValues(String name) {
		List<?> argValues = this.source.valuesOf(name);
		List<String> stringArgValues = new ArrayList<>();
		for (Object argValue : argValues) {
			stringArgValues.add(argValue.toString());
		}
		if (stringArgValues.isEmpty()) {
			return (this.source.has(name) ? Collections.emptyList() : null);
		}
		return Collections.unmodifiableList(stringArgValues);
	}

	@Override
	protected List<String> getNonOptionArgs() {
		List<?> argValues = this.source.nonOptionArguments();
		List<String> stringArgValues = new ArrayList<>();
		for (Object argValue : argValues) {
			stringArgValues.add(argValue.toString());
		}
		return (stringArgValues.isEmpty() ? Collections.emptyList() :
				Collections.unmodifiableList(stringArgValues));
	}

}
```

## `MutablePropertySources`

`PropertySources` 的默认实现如下，增加了添加、删除和替代 `PropertySource` 功能。

```java
public class MutablePropertySources implements PropertySources {

	private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();

	public MutablePropertySources() {
	}

	public MutablePropertySources(PropertySources propertySources) {
		this();
		for (PropertySource<?> propertySource : propertySources) {
			addLast(propertySource);
		}
	}


	@Override
	public Iterator<PropertySource<?>> iterator() {
		return this.propertySourceList.iterator();
	}

	@Override
	public Spliterator<PropertySource<?>> spliterator() {
		return Spliterators.spliterator(this.propertySourceList, 0);
	}

	@Override
	public Stream<PropertySource<?>> stream() {
		return this.propertySourceList.stream();
	}

	@Override
	public boolean contains(String name) {
		for (PropertySource<?> propertySource : this.propertySourceList) {
			if (propertySource.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Nullable
	public PropertySource<?> get(String name) {
		for (PropertySource<?> propertySource : this.propertySourceList) {
			if (propertySource.getName().equals(name)) {
				return propertySource;
			}
		}
		return null;
	}

	public void addFirst(PropertySource<?> propertySource) {
		synchronized (this.propertySourceList) {
			removeIfPresent(propertySource);
			this.propertySourceList.add(0, propertySource);
		}
	}

	public void addLast(PropertySource<?> propertySource) {
		synchronized (this.propertySourceList) {
			removeIfPresent(propertySource);
			this.propertySourceList.add(propertySource);
		}
	}

	public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		synchronized (this.propertySourceList) {
			removeIfPresent(propertySource);
			int index = assertPresentAndGetIndex(relativePropertySourceName);
			addAtIndex(index, propertySource);
		}
	}

	public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		synchronized (this.propertySourceList) {
			removeIfPresent(propertySource);
			int index = assertPresentAndGetIndex(relativePropertySourceName);
			addAtIndex(index + 1, propertySource);
		}
	}

	public int precedenceOf(PropertySource<?> propertySource) {
		return this.propertySourceList.indexOf(propertySource);
	}

	@Nullable
	public PropertySource<?> remove(String name) {
		synchronized (this.propertySourceList) {
			int index = this.propertySourceList.indexOf(PropertySource.named(name));
			return (index != -1 ? this.propertySourceList.remove(index) : null);
		}
	}

	public void replace(String name, PropertySource<?> propertySource) {
		synchronized (this.propertySourceList) {
			int index = assertPresentAndGetIndex(name);
			this.propertySourceList.set(index, propertySource);
		}
	}

	public int size() {
		return this.propertySourceList.size();
	}

}
```

## `PropertySourcesPropertyResolver`

`PropertySourcesPropertyResolver` 提供了 `PropertyResolver` 的默认实现。

`PropertySourcesPropertyResolver` 会通过 `PropertySources` 依次遍历所有数据源查找配置。

```java
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

	@Nullable
	private final PropertySources propertySources;

	/**
	 * Create a new resolver against the given property sources.
	 * @param propertySources the set of {@link PropertySource} objects to use
	 */
	public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
		this.propertySources = propertySources;
	}


	@Override
	public boolean containsProperty(String key) {
		if (this.propertySources != null) {
			for (PropertySource<?> propertySource : this.propertySources) {
				if (propertySource.containsProperty(key)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@Nullable
	public String getProperty(String key) {
		return getProperty(key, String.class, true);
	}

	@Override
	@Nullable
	public <T> T getProperty(String key, Class<T> targetValueType) {
		return getProperty(key, targetValueType, true);
	}

	@Override
	@Nullable
	protected String getPropertyAsRawString(String key) {
		return getProperty(key, String.class, false);
	}

	@Nullable
	protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
		if (this.propertySources != null) {
			for (PropertySource<?> propertySource : this.propertySources) {
				if (logger.isTraceEnabled()) {
					logger.trace("Searching for key '" + key + "' in PropertySource '" +
							propertySource.getName() + "'");
				}
				Object value = propertySource.getProperty(key);
				if (value != null) {
					if (resolveNestedPlaceholders && value instanceof String) {
						value = resolveNestedPlaceholders((String) value);
					}
					logKeyFound(key, propertySource, value);
					return convertValueIfNecessary(value, targetValueType);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Could not find key '" + key + "' in any property source");
		}
		return null;
	}

}
```

