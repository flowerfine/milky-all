# 配置DSL

`领域特定语言（domain-specific language, DSL）`是一门模仿特定领域专业人员所熟知的属于、习惯用法和表达方式的编程语言。精心设计的 DSL 语言具有很好的封装性，隐藏实现细节，对外只暴露需要在应用编写或修改代码的所需工作量。

虽然 DSL 很强大，但是开发成本非常高，而且对设计的要求非常高。每个 DSL 都是一门独一无二的语言，难以找到一个合适的抽象。良好的 DSL 可以帮助构建可伸缩的、健壮的应用程序。

`DSL` 分为内部和外部两种。外部 DSL 是拥有自身的语法和分析器的自定义语言，如 `elasticsearch` 的 [`Query DSL`](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)：

```json
GET /_search
{
  "query": { 
    "bool": { 
      "must": [
        { "match": { "title":   "Search"        }},
        { "match": { "content": "Elasticsearch" }}
      ],
      "filter": [ 
        { "term":  { "status": "published" }},
        { "range": { "publish_date": { "gte": "2015-01-01" }}}
      ]
    }
  }
}
```

而内部 DSL 是一门通用的编程语言，不需要特定用途的分析器，但是底层语言也约束了对特定领域的概念的表达，如 `spring security` 的 [DSL](https://spring.io/blog/2019/11/21/spring-security-lambda-dsl)：

```java
// lambda style
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers("/blog/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .permitAll()
            )
            .rememberMe(withDefaults());
    }
}

// none lambda style
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/blog/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .rememberMe();
    }
}
```

## 可配置就一定可编程

因为应用程序的环境的不确定，框架总免不了使用配置，最常见的莫过于开发、测试、灰度和生产环境需要连接不同的数据库连接。

配置和编程是一码事，问题简单了可以通过配置解决，问题复杂了就只能通过编程，二者并没有一个明显的界限。但是二者有一个先后顺序：先有编程再有配置，反过来说配置文件能做的就一定要能够通过编程方式进行，否则当出现框架间集成的时候就一定会引入问题。

`spring security` 提供了一个体验非常良好的配置 `DSL`，下面是参考 `spring security` 提取的一个配置 `DSL` 原型。

## Builder

在构造具有很长列表的参数对象时*建造者模式*是一大利器，在配置领域不能视而不见。

```java
public interface Builder<O> {

    O build() throws Exception;
}
```

## Configurer

使用建造者可以完成所有参数的配置，缺点是一个长长的设置参数列表。如下是 `springfox` 的 `Docket` 的部分代码：

```java
public class Docket implements DocumentationPlugin {
	
    // ......
    
    public DocumentationContext configure(DocumentationContextBuilder builder) {
        return builder
            .apiInfo(apiInfo)
            .selector(apiSelector)
            .applyDefaultResponseMessages(applyDefaultResponseMessages)
            .additionalResponseMessages(responseMessages)
            .additionalResponses(responses)
            .additionalOperationParameters(globalOperationParameters)
            .additionalRequestParameters(globalRequestParameters)
            .additionalIgnorableTypes(ignorableParameterTypes)
            .ruleBuilders(ruleBuilders)
            .groupName(groupName)
            .pathProvider(pathProvider)
            .securityContexts(securityContexts)
            .securitySchemes(securitySchemes)
            .apiListingReferenceOrdering(apiListingReferenceOrdering)
            .apiDescriptionOrdering(apiDescriptionOrdering)
            .operationOrdering(operationOrdering)
            .produces(produces)
            .consumes(consumes)
            .host(host)
            .protocols(protocols)
            .genericsNaming(genericsNamingStrategy)
            .pathMapping(pathMapping)
            .enableUrlTemplating(enableUrlTemplating)
            .additionalModels(additionalModels)
            .tags(tags)
            .vendorExtentions(vendorExtensions)
            .servers(servers)
            .build();
    }
    
    // ......
}
```

`Docket` 本身就是一个基于建造者实现的配置参数工具，而在 `springfox` 内部获取用户配置的信息的时候又是一串长长的 `getter/setter` 代码。

`spring security` 对参数进行功能上的划分创建不同的 `Configurer`，并通过巧妙的方式来切换，用户使用的时候通过缩进组织参数配置。

```java
public interface Configurer<O, B extends Builder<O>> {

    void init(B builder) throws Exception;

    void configure(B builder) throws Exception;
}
```

在 `Configurer` 中进行的参数配置最终会通过 `#configure(Builder)` 传递到 `Builder` 中去。

## ConfigurableBuilder

将 `Builder` 和 `Configurer` 结合在一起的是 `ConfigurableBuilder` 接口。

```java
public interface ConfigurableBuilder<O, H extends ConfigurableBuilder<O, H>> extends Builder<O> {

    <C extends Configurer<O, H>> C getConfigurer(Class<C> clazz);

    <C extends Configurer<O, H>> C removeConfigurer(Class<C> clazz);
}
```

## AbstractBuilder

```java
public abstract class AbstractBuilder<O> implements Builder<O> {

    private AtomicBoolean started = new AtomicBoolean();

    private O object;

    @Override
    public final O build() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.object = doBuild();
            return this.object;
        }
        throw new AlreadyBuiltException("This object has already been started to build");
    }

    public final O getObject() {
        if (!this.started.get()) {
            throw new IllegalStateException("This object has not been started to build");
        }
        return this.object;
    }

    protected abstract O doBuild() throws Exception;
}
```

`Builder` 接口的实现 `AbstractBuilder` 提供了对象的缓存功能，防止对象的重复创建，并可以通过 `#getObject` 方法实现一次创建多次访问。

## AbstractConfigurer

```java
public abstract class AbstractConfigurer<O, B extends Builder<O>> implements Configurer<O, B> {

    private B builder;

    @Override
    public void init(B builder) throws Exception {

    }

    @Override
    public void configure(B builder) throws Exception {

    }

    public B and() {
        return getBuilder();
    }

    protected final B getBuilder() {
        checkState(this.builder != null, () -> "builder cannot be null");
        return this.builder;
    }

    public void setBuilder(B builder) {
        this.builder = builder;
    }
}
```

`Configurer` 接口的实现 `AbstractConfigurer` 提供了在不同 `Configurer` 间切换的功能： `#and()` 方法。在每次调用 `#and()` 方法后能够从当前 `Configurer` 切换到 `Builder` 中。

## AbstractConfiguredBuilder

```java
public abstract class AbstractConfiguredBuilder<O, B extends ConfigurableBuilder<O, B>> extends AbstractBuilder<O> implements ConfigurableBuilder<O, B> {
    
    /**
     * 使用 {@link LinkedHashMap} 的原因是为了保证配置顺序与执行顺序一致。
     */
    private final Map<Class<? extends Configurer<O, B>>, Configurer<O, B>> configurers = new LinkedHashMap<>();

    private BuildState buildState = BuildState.UNBUILT;

    public O getOrBuild() {
        if (!isUnbuilt()) {
            return getObject();
        }
        try {
            return build();
        } catch (Exception ex) {
            this.log.debug("Failed to perform build. Returning null", ex);
            return null;
        }
    }

    @Override
    protected final O doBuild() throws Exception {
        synchronized (this.configurers) {
            this.buildState = BuildState.INITIALIZING;
            beforeInit();
            init();
            this.buildState = BuildState.CONFIGURING;
            beforeConfigure();
            configure();
            this.buildState = BuildState.BUILDING;
            O result = performBuild();
            this.buildState = BuildState.BUILT;
            return result;
        }
    }

    protected void beforeInit() throws Exception {
    }

    protected void beforeConfigure() throws Exception {
    }

    protected abstract O performBuild() throws Exception;

    @SuppressWarnings("unchecked")
    private void init() throws Exception {
        Collection<Configurer<O, B>> configurers = getConfigurers();
        for (Configurer<O, B> configurer : configurers) {
            configurer.init((B) this);
        }
    }

    @SuppressWarnings("unchecked")
    private void configure() throws Exception {
        Collection<Configurer<O, B>> configurers = getConfigurers();
        for (Configurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends AbstractConfigurer<O, B>> C apply(C configurer) throws Exception {
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

    public <C extends Configurer<O, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    @SuppressWarnings("unchecked")
    private <C extends Configurer<O, B>> void add(C configurer) {
        checkNotNull(configurer, () -> "configurer cannot be null");

        Class<? extends Configurer<O, B>> clazz = (Class<? extends Configurer<O, B>>) configurer.getClass();
        synchronized (this.configurers) {
            checkState(!this.buildState.isConfigured(), () -> "Cannot apply " + configurer + " to already built object");

            this.configurers.put(clazz, configurer);
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends Configurer<O, B>> C getConfigurer(Class<C> clazz) {
        return (C) this.configurers.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <C extends Configurer<O, B>> C removeConfigurer(Class<C> clazz) {
        return (C) this.configurers.remove(clazz);
    }

    private Collection<Configurer<O, B>> getConfigurers() {
        return this.configurers.values();
    }

    private boolean isUnbuilt() {
        synchronized (this.configurers) {
            return this.buildState == BuildState.UNBUILT;
        }
    }

    /**
     * The build state for the application
     */
    private enum BuildState {

        UNBUILT(0),
        INITIALIZING(1),
        CONFIGURING(2),
        BUILDING(3),
        BUILT(4);

        private final int order;
        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == this.order;
        }
        public boolean isConfigured() {
            return this.order >= CONFIGURING.order;
        }

    }
}
```

`ConfigurableBuilder` 接口的实现 `AbstractConfiguredBuilder` 一方面扩展了 `AbstractBuilder` 的对象的构造声明周期并支持 `Configurer` 的 `#init()` 和 `#configure()` 功能，另一方面通过 `#apply` 方法支持为 `Builder` 添加 `Configurer`。

到此为止，这套配置 DSL 的使用方式就出来了：

* 调用 `AbstractConfiguredBuilder` 的 `#apply` 方法添加 `Configurer` 对象用于后面增强 `Builder`。
* 调用 `Builder#build()` 或 `AbstractConfiguredBuilder#getOrBuild()` 方法，触发对象构造的生命周期调用。
* 在 `AbstractBuilder#doBuild()` 方法实现中，先是调用 `Configurer#init()` 和 `Configurer#configurer(Builder)` 方法，然后通过 `#performBuild()` 方法执行真正的对象创建。

那么对于需要使用这套配置 `DSL` 的使用者来说它们需要做的事情就出来了：

* 继承 `AbstractConfiguredBuilder` ，像正常的编码建造者一样设置一系列属性的 `getter/setter` 方法，并在 `#perfomBuild()` 方法中 `new` 这个对象。
* 继承 `AbstractConfigurer` 接口，按照功能划分在 `AbstractConfigurer` 实现类中完成参数设置，并在 `#configure(Builder)` 方法中将这些参数传递给 `Builder` 。
* 在 `AbstractConfigredBuilder` 的实现类中为用户提供添加自定义 `Configurer` 实现类的方法。

## CompositeBuilder 案例

这里使用这套配置 `DSL` 创建并配置这个 `Composite` 对象。

```java
public class Composite {

    private String foo;
    private String bar;
    private String subBar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getSubBar() {
        return subBar;
    }

    public void setSubBar(String subBar) {
        this.subBar = subBar;
    }

    @Override
    public String toString() {
        return "Composite{" +
                "foo='" + foo + '\'' +
                ", bar='" + bar + '\'' +
                ", subBar='" + subBar + '\'' +
                '}';
    }
}
```

首先是实现 `AbstractConfiguredBuilder`，这里简单地提供了 `getter/setter` 方法用于设置属性：

```java
public class CompositeBuilder extends AbstractConfiguredBuilder<Composite, CompositeBuilder> implements Builder<Composite> {

    private String foo;
    private String bar;
    private String subBar;

    @Override
    protected Composite performBuild() throws Exception {
        final Composite composite = new Composite();
        composite.setFoo(foo);
        composite.setBar(bar);
        composite.setSubBar(subBar);
        return composite;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public void setSubBar(String subBar) {
        this.subBar = subBar;
    }
}
```

然后对于 `foo`，`bar`，`subBar` 属性添加三个 `Configurer` 用于添加参数：

```java
public abstract class AbstractCompositeConfigurer<T extends AbstractCompositeConfigurer<T, B>, B extends ConfigurableBuilder<Composite, B>> extends AbstractConfigurer<Composite, B> {

}

public class FooConfigurer extends AbstractCompositeConfigurer<FooConfigurer, CompositeBuilder> {

    private String foo;

    public FooConfigurer foo(String foo) {
        notBlank(foo, () -> "foo cannot be blank");

        this.foo = foo;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setFoo(foo);
        System.out.println("FooConfigurer configure composite with " + foo);
    }
}

public class BarConfigurer extends AbstractCompositeConfigurer<BarConfigurer, CompositeBuilder> {

    private String bar;

    public BarConfigurer bar(String bar) {
        notBlank(bar, () -> "bar cannot be blank");

        this.bar = bar;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setBar(bar);
        System.out.println("BarConfigurer configure composite with " + bar);

        SubBarConfigurer subBarConfigurer = composite.getConfigurer(SubBarConfigurer.class);
        if (subBarConfigurer != null) {
            subBarConfigurer.subBar("sub bar property");
        }
    }
}

public class SubBarConfigurer extends AbstractCompositeConfigurer<SubBarConfigurer, CompositeBuilder> {

    private String subBar;

    public SubBarConfigurer subBar(String subBar) {
        notBlank(subBar, () -> "subBar cannot be blank");

        this.subBar = subBar;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setSubBar(subBar);
        System.out.println("SubBarConfigurer configure composite with " + subBar);
    }
}
```

在 `FooConfigurer`，`BarConfigurer` 和 `SubBarConfigurer` 中使用了建造者风格的设置参数的方法 `#foo`，`#bar`，`#subBar`，并在各自的 `#configurer(Builder)` 方法中将这些参数在丢回到 `CompositeBuilder` 中。

在 `CompositeBuilder` 中添加用于应用这些 `Configurer` 的方法：

```java
public class CompositeBuilder extends AbstractConfiguredBuilder<Composite, CompositeBuilder> implements Builder<Composite> {

    public FooConfigurer foo() throws Exception {
        return getOrApply(new FooConfigurer());
    }

    public BarConfigurer bar() throws Exception {
        return getOrApply(new BarConfigurer());
    }

    public SubBarConfigurer subBar() throws Exception {
        return getOrApply(new SubBarConfigurer());
    }

    private <C extends AbstractConfigurer<Composite, CompositeBuilder>> C getOrApply(C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }
}
```

最后使用效果如下：

```java
@Test
void testCompositeConfig() throws Exception {
    CompositeBuilder config = new CompositeBuilder();
    config.foo()
            .foo("foo property")
            .and()
          .bar()
            .bar("bar property")
            .and()
          .subBar();

    final Composite composite = config.getOrBuild();
    System.out.println(composite);
}
```

## lambda 支持

`lambda` 的支持也非常简单。定义接口 `Customizer` 如下：

```java
@FunctionalInterface
public interface Customizer<T> {

    void customize(T t);

    static <T> Customizer<T> withDefaults() {
        return (t) -> {};
    }
}
```

在 `CompositeBuilder` 中增加 lambda 风格的方法：

```java
public class CompositeBuilder extends AbstractConfiguredBuilder<Composite, CompositeBuilder> implements Builder<Composite> {

    public CompositeBuilder foo(Customizer<FooConfigurer> fooConfigurerCustomizer) throws Exception {
        fooConfigurerCustomizer.customize(getOrApply(new FooConfigurer()));
        return CompositeBuilder.this;
    }

    public CompositeBuilder bar(Customizer<BarConfigurer> barConfigurerCustomizer) throws Exception {
        barConfigurerCustomizer.customize(getOrApply(new BarConfigurer()));
        return CompositeBuilder.this;
    }

    public CompositeBuilder subBar(Customizer<SubBarConfigurer> subBarConfigurerCustomizer) throws Exception {
        subBarConfigurerCustomizer.customize(getOrApply(new SubBarConfigurer()));
        return CompositeBuilder.this;
    }

    private <C extends AbstractConfigurer<Composite, CompositeBuilder>> C getOrApply(C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }
}
```

使用效果如下：

```java
@Test
void testLambdaCompositeConfig() throws Exception {
    CompositeBuilder config = new CompositeBuilder();
    config.foo(fooConfigurer -> fooConfigurer.foo("foo property"))
            .subBar(Customizer.withDefaults())
            .bar(barConfigurer -> barConfigurer.bar("bar property"));

    final Composite composite = config.getOrBuild();
    System.out.println(composite);
}
```

## 总结

这套 DSL 对内封装细节，对外提供了良好的使用体验，属于对使用方友好，而对 DSL 的维护者不友好的。拿上面的 `CompositeBuilder` 案例来说，它本身的可以通过建造者模式就可以实现参数的配置，但是为了增强使用体验，又扩展出了多个 `Configurer`，为内部开发增加了复杂性。

所以是否使用需要开发者去平衡复杂性和使用体验。