# nifi 标准 `Processor` 设计

在数据引擎中，往往期望能够提供标准的 `processor` 实现对数据的 `route`、`transformation`，从而将常见的对数据的读取，写入，过滤，转化处理等功能进行标准化，编排 `processor` 实现 `ETL` 的快速实现。

通过 `sql` 化或者组件拖拉拽的方式能够让不需要了解数据引擎的基础上快速地实现 `ETL` 工作。

nifi 即是一个令人耳目一新的数据平台。

本文介绍 nifi 标准化组件的设计。



## 配置

配置文件读取位于 `nifi/nifi-commons/nifi-property-utils` 和 `nifi/nifi-commons/nifi-properties` 模块中，读取 `bootstrap.conf` 和 `nifi.properties` 配置文件的内容进 `Properties` 类中。



### `PropertyDescriptor`

标准组件需要的属性可以通过 `PropertyDescriptor` 进行声明。

```java
/**
 * An immutable object for holding information about a type of component
 * property.
 *
 */
public final class PropertyDescriptor implements Comparable<PropertyDescriptor> {

   /**
     * The proper name for the property. This is the primary mechanism of
     * comparing equality.
     */
    private final String name;
    /**
     * The name that should be displayed to user when referencing this property
     */
    private final String displayName;
    /**
     * And explanation of the meaning of the given property. This description is
     * meant to be displayed to a user or simply provide a mechanism of
     * documenting intent.
     */
    private final String description;
    /**
     * The default value for this property
     */
    private final String defaultValue;
    /**
     * The allowable values for this property. If empty then the allowable
     * values are not constrained
     */
    private final List<AllowableValue> allowableValues;
    /**
     * Determines whether the property is required for this processor
     */
    private final boolean required;
    /**
     * indicates that the value for this property should be considered sensitive
     * and protected whenever stored or represented
     */
    private final boolean sensitive;
    /**
     * indicates whether this property well-known for this processor or is
     * user-defined
     */
    private final boolean dynamic;
    /**
     * indicates whether or nor this property will evaluate expression language
     * against the flow file attributes
     */
    private final ExpressionLanguageScope expressionLanguageScope;
    /**
     * indicates whether or not this property represents resources that should be added
     * to the classpath and used for loading native libraries for this instance of the component
     */
    private final boolean dynamicallyModifiesClasspath;
    /**
     * the interface of the {@link ControllerService} that this Property refers
     * to; will be null if not identifying a ControllerService.
     */
    private final Class<? extends ControllerService> controllerServiceDefinition;
    /**
     * The validators that should be used whenever an attempt is made to set
     * this property value. Any allowable values specified will be checked first
     * and any validators specified will be ignored.
     */
    private final List<Validator> validators;
    /**
     * The list of dependencies that this property has on other properties
     */
    private final Set<PropertyDependency> dependencies;
    /**
     * The definition of the resource(s) that this property references
     */
    private final ResourceDefinition resourceDefinition;
}
```

* 属性的名称，描述。nifi 组件声明的属性在页面拖拉拽配置的时候可以自动展示在表单中，属性名称和描述可以帮助使用者了解属性含义。
* 默认值。默认值支持。
* 允许值。对于具有枚举含义的属性，仅能选择允许的属性值。
* 敏感数据，是否必须，动态的。对于敏感数据如数据库用户名密码，需要小心处理，有些属性则不是必须的，比如数据库连接池大小，当未提供时可以使用默认值，但是对于数据库用户名密码等信息就是不可或缺的。属性值是否能动态修改，比如数据库用户名密码就不允许动态修改，但是像数据处理速率就可以支持动态修改，简单轻量地对运行的任务进行干预。
* 表达式作用域。很多时候需要从处理的数据中提取一些字段值用以执行任务，比如 sql 任务中提取字段值作为 sql 执行的参数等，这个提取的过程即通过表达式进行。nifi 对数据也进行了标准化处理，抽象成 `FlowFile`，表达式作用域说明表达式会对那些标准数据进行字段值提取。
* `controllerServiceDefinition`。nifi 的标准组件有两种：`processor` 和 `controllerService`，类似于单例和多例，`processor` 在每个流程中都会创建新的实例，而有些功能则不需要比如提供 http 服务，jdbc 服务等，可以供多个 `processor` 实例进行共享。`processor` 可以声明 `controllerService` 类型的属性来使用共享的 `controllerService` 服务。
* 属性校验器。用于对属性进行校验。
* 属性依赖。属性与属性之间也会存在依赖关系。
* 资源。`processor` 声明需要用到的资源。

### `PropertyValue`

`PropertyValue` 为属性值的抽象。提供了很多的工具方法用于对属性值进行转换，以及表达式求值方法。

```java
/**
 * <p>
 * A PropertyValue provides a mechanism whereby the currently configured value
 * of a processor property can be obtained in different forms.
 * </p>
 */
public interface PropertyValue {

    /**
     * @return the raw property value as a string
     */
    String getValue();

    /**
     * @return an integer representation of the property value, or
     * <code>null</code> if not set
     * @throws NumberFormatException if not able to parse
     */
    Integer asInteger();

    /**
     * @return a Long representation of the property value, or <code>null</code>
     * if not set
     * @throws NumberFormatException if not able to parse
     */
    Long asLong();

    /**
     * @return a Boolean representation of the property value, or
     * <code>null</code> if not set
     */
    Boolean asBoolean();

    /**
     * @return a Float representation of the property value, or
     * <code>null</code> if not set
     * @throws NumberFormatException if not able to parse
     */
    Float asFloat();

    /**
     * @return a Double representation of the property value, of
     * <code>null</code> if not set
     * @throws NumberFormatException if not able to parse
     */
    Double asDouble();

    /**
     * @param timeUnit specifies the TimeUnit to convert the time duration into
     * @return a Long value representing the value of the configured time period
     * in terms of the specified TimeUnit; if the property is not set, returns
     * <code>null</code>
     */
    Long asTimePeriod(TimeUnit timeUnit);

    /**
     *
     * @param dataUnit specifies the DataUnit to convert the data size into
     * @return a Long value representing the value of the configured data size
     * in terms of the specified DataUnit; if hte property is not set, returns
     * <code>null</code>
     */
    Double asDataSize(DataUnit dataUnit);

    /**
     * @return the ControllerService whose identifier is the raw value of
     * <code>this</code>, or <code>null</code> if either the value is not set or
     * the value does not identify a ControllerService
     */
    ControllerService asControllerService();

    /**
     * @param <T> the generic type of the controller service
     * @param serviceType the class of the Controller Service
     * @return the ControllerService whose identifier is the raw value of the
     * <code>this</code>, or <code>null</code> if either the value is not set or
     * the value does not identify a ControllerService. The object returned by
     * this method is explicitly cast to type specified, if the type specified
     * is valid. Otherwise, throws an IllegalArgumentException
     *
     * @throws IllegalArgumentException if the value of <code>this</code> points
     * to a ControllerService but that service is not of type
     * <code>serviceType</code> or if <code>serviceType</code> references a
     * class that is not an interface
     */
    <T extends ControllerService> T asControllerService(Class<T> serviceType) throws IllegalArgumentException;

    /**
     * @return a ResourceReference for the configured property value, or <code>null</code> if no value was specified, or if the property references multiple resources.
     * @see #asResources()
     */
    ResourceReference asResource();

    /**
     * @return a ResourceReferences for the configured property value. If no property value is set, a ResourceRferences will be returned that references no resources.
     * I.e., this method will never return <code>null</code>.
     */
    ResourceReferences asResources();

    /**
     * @return <code>true</code> if the user has configured a value, or if the
     * {@link PropertyDescriptor} for the associated property has a default
     * value, <code>false</code> otherwise
     */
    boolean isSet();

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language;
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @return a PropertyValue with the new value is returned, supporting call
     * chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions() throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language;
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param attributes a Map of attributes that the Expression can reference.
     * These will take precedence over any underlying variable registry values.
     *
     * @return a PropertyValue with the new value
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(Map<String, String> attributes) throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language.
     * The supplied decorator is then given a chance to decorate the value, and
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param attributes a Map of attributes that the Expression can reference.
     * These will take precedence over any variables in any underlying variable
     * registry.
     * @param decorator the decorator to use in order to update the values
     * returned after variable substitution and expression language evaluation.
     *
     * @return a PropertyValue with the new value
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(Map<String, String> attributes, AttributeValueDecorator decorator) throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language;
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param flowFile to evaluate attributes of. It's flow file properties and
     * then flow file attributes will take precedence over any underlying
     * variable registry.
     * @return a PropertyValue with the new value is returned, supporting call
     * chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(FlowFile flowFile) throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language;
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param flowFile to evaluate attributes of. It's flow file properties and
     * then flow file attributes will take precedence over any underlying
     * variable registry.
     * @param additionalAttributes a Map of additional attributes that the
     * Expression can reference. These attributes will take precedence over any
     * conflicting attributes in the provided flowfile or any underlying
     * variable registry.
     *
     * @return a PropertyValue with the new value is returned, supporting call
     * chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(FlowFile flowFile, Map<String, String> additionalAttributes) throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language;
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param flowFile to evaluate attributes of. It's flow file properties and
     * then flow file attributes will take precedence over any underlying
     * variable registry.
     * @param additionalAttributes a Map of additional attributes that the
     * Expression can reference. These attributes will take precedence over any
     * conflicting attributes in the provided flowfile or any underlying
     * variable registry.
     * @param decorator the decorator to use in order to update the values
     * returned after variable substitution and expression language evaluation.
     *
     * @return a PropertyValue with the new value is returned, supporting call
     * chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(FlowFile flowFile, Map<String, String> additionalAttributes, AttributeValueDecorator decorator) throws ProcessException;


    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression
     * Language; a PropertyValue with the new value is then returned, supporting
     * call chaining.
     * </p>
     *
     * @param flowFile to evaluate attributes of
     * @param additionalAttributes a Map of additional attributes that the Expression can reference. If entries in
     * this Map conflict with entries in the FlowFile's attributes, the entries in this Map are given a higher priority.
     * @param decorator the decorator to use in order to update the values returned by the Expression Language
     * @param stateValues a Map of the state values to be referenced explicitly by specific state accessing functions
     *
     * @return a PropertyValue with the new value is returned, supporting call
     * chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or evaluating
     * the Expression against the given attributes causes an Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(FlowFile flowFile, Map<String, String> additionalAttributes, AttributeValueDecorator decorator, Map<String, String> stateValues)
            throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language.
     * The supplied decorator is then given a chance to decorate the value, and
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param decorator the decorator to use in order to update the values
     * returned after variable substitution and expression language evaluation.
     * @return a PropertyValue with the new value is then returned, supporting
     * call chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(AttributeValueDecorator decorator) throws ProcessException;

    /**
     * <p>
     * Replaces values in the Property Value using the NiFi Expression Language.
     * The supplied decorator is then given a chance to decorate the value, and
     * a PropertyValue with the new value is then returned, supporting call
     * chaining. Before executing the expression language statement any
     * variables names found within any underlying {@link VariableRegistry} will
     * be substituted with their values.
     * </p>
     *
     * @param flowFile to evaluate expressions against
     * @param decorator the decorator to use in order to update the values
     * returned after variable substitution and expression language evaluation.
     *
     *
     * @return a PropertyValue with the new value is then returned, supporting
     * call chaining
     *
     * @throws ProcessException if the Expression cannot be compiled or
     * evaluating the Expression against the given attributes causes an
     * Exception to be thrown
     */
    PropertyValue evaluateAttributeExpressions(FlowFile flowFile, AttributeValueDecorator decorator) throws ProcessException;

    /**
     * <p>
     * Indicates whether the value of the property uses Expression Language.
     * </p>
     *
     * @return <code>true</code> if the property value makes use of the Expression Language, <code>false</code> otherwise.
     */
    boolean isExpressionLanguagePresent();
}
```

### `PropertyDependency`

```java
public class PropertyDependency {

    /**
     * the name of the property that is depended upon
     */
    private final String propertyName;

    /**
     * the display name of the property that is depended upon
     */
    private final String displayName;

    /**
     * the values that satisfy the dependency
     */
    private final Set<String> dependentValues;
}
```

### `AllowableValue`

```java
public class AllowableValue {

    private final String value;
    private final String displayName;
    private final String description;
}
```

### `PropertyContext`

`PropertyContext` 接口提供了读取属性值的入口。

```java
/**
 * A context for retrieving a PropertyValue from a PropertyDescriptor.
 */
public interface PropertyContext {

    /**
     * Retrieves the current value set for the given descriptor, if a value is
     * set - else uses the descriptor to determine the appropriate default value
     *
     * @param descriptor to lookup the value of
     * @return the property value of the given descriptor
     */
    PropertyValue getProperty(PropertyDescriptor descriptor);

    Map<String,String> getAllProperties();
}
```

`ProcessContext` 继承 `PropertyContext`，提供了 `Processor` 访问 nifi 属性的功能。

```java
/**
 * <p>
 * Provides a bridge between a Processor and the NiFi Framework
 * </p>
 *
 * <p>
 * <b>Note: </b>Implementations of this interface are NOT necessarily
 * thread-safe.
 * </p>
 */
public interface ProcessContext extends PropertyContext, ClusterContext {

    /**
     * Creates and returns a {@link PropertyValue} object that can be used for
     * evaluating the value of the given String
     *
     * @param rawValue the raw input before any property evaluation has occurred
     * @return a {@link PropertyValue} object that can be used for
     * evaluating the value of the given String
     */
    PropertyValue newPropertyValue(String rawValue);

    /**
     * Retrieves the current value set for the given descriptor, if a value is
     * set - else uses the descriptor to determine the appropriate default value
     *
     * @param propertyName of the property to lookup the value for
     * @return property value as retrieved by property name
     */
    PropertyValue getProperty(String propertyName);

    /**
     * @return a Map of all PropertyDescriptors to their configured values. This
     * Map may or may not be modifiable, but modifying its values will not
     * change the values of the processor's properties
     */
    Map<PropertyDescriptor, String> getProperties();

    /**
     * Encrypts the given value using the password provided in the NiFi
     * Properties
     *
     * @param unencrypted plaintext value
     * @return encrypted value
     */
    String encrypt(String unencrypted);

    /**
     * Decrypts the given value using the password provided in the NiFi
     * Properties
     *
     * @param encrypted the encrypted value
     * @return the plaintext value
     */
    String decrypt(String encrypted);

    /**
     * @param property the Property whose value should be inspected to determined if it contains an Expression Language Expression
     * @return <code>true</code> if the value of the given Property contains a NiFi Expression
     * Language Expression, <code>false</code> if it does not. Note that <code>false</code> will be returned if the Property Descriptor
     * does not allow the Expression Language, even if a seemingly valid Expression is present in the value.
     */
    boolean isExpressionLanguagePresent(PropertyDescriptor property);
}
```

## 组件

nifi 提供组件定义的接口 `ConfigurableComponent`，它有两个子接口，分别为 `Processor` 和 `ControllerService`。

```java
public interface ConfigurableComponent {

    /**
     * @return the unique identifier that the framework assigned to this
     * component
     */
    String getIdentifier();

    /**
     * @param name to lookup the descriptor
     * @return the PropertyDescriptor with the given name, if it exists;
     * otherwise, returns <code>null</code>
     */
    PropertyDescriptor getPropertyDescriptor(String name);

    /**
     * Returns a {@link List} of all {@link PropertyDescriptor}s that this
     * component supports.
     *
     * @return PropertyDescriptor objects this component currently supports
     */
    List<PropertyDescriptor> getPropertyDescriptors();

    /**
     * Hook method allowing subclasses to eagerly react to a configuration
     * change for the given property descriptor. This method will be invoked
     * regardless of property validity. As an alternative to using this method,
     * a component may simply get the latest value whenever it needs it and if
     * necessary lazily evaluate it. Any throwable that escapes this method will
     * simply be ignored.
     *
     * When NiFi is restarted, this method will be called for each 'dynamic' property that is
     * added, as well as for each property that is not set to the default value. I.e., if the
     * Properties are modified from the default values. If it is undesirable for your use case
     * to react to properties being modified in this situation, you can add the {@link OnConfigurationRestored}
     * annotation to a method - this will allow the Processor to know when configuration has
     * been restored, so that it can determine whether or not to perform some action in the
     * onPropertyModified method.
     *
     * @param descriptor the descriptor for the property being modified
     * @param oldValue the value that was previously set, or null if no value
     *            was previously set for this property
     * @param newValue the new property value or if null indicates the property
     *            was removed
     */
    void onPropertyModified(PropertyDescriptor descriptor, String oldValue, String newValue);

    /**
     * Returns a {@link List} of all {@link PropertyDescriptor}s that this
     * component supports.
     *
     * @return PropertyDescriptor objects this component currently supports
     */
    List<PropertyDescriptor> getPropertyDescriptors();

}
```

### `AbstractConfigurableComponent`

`AbstractConfigurableComponent` 提供 `ConfigurableComponent` 基础实现，主要是提供属性查询和属性验证的功能。

```java
public abstract class AbstractConfigurableComponent implements ConfigurableComponent {

    /**
     * @param descriptorName to lookup the descriptor
     * @return a PropertyDescriptor for the name specified that is fully
     * populated
     */
    @Override
    public final PropertyDescriptor getPropertyDescriptor(final String descriptorName) {
        final PropertyDescriptor specDescriptor = new PropertyDescriptor.Builder().name(descriptorName).build();
        return getPropertyDescriptor(specDescriptor);
    }

    @Override
    public final List<PropertyDescriptor> getPropertyDescriptors() {
        final List<PropertyDescriptor> supported = getSupportedPropertyDescriptors();
        return supported == null ? Collections.emptyList() : new ArrayList<>(supported);
    }

    private PropertyDescriptor getPropertyDescriptor(final PropertyDescriptor specDescriptor) {
        //check if property supported
        PropertyDescriptor descriptor = getSupportedPropertyDescriptor(specDescriptor);
        if (descriptor != null) {
            return descriptor;
        }

        descriptor = getSupportedDynamicPropertyDescriptor(specDescriptor.getName());
        if (descriptor != null && !descriptor.isDynamic()) {
            descriptor = new PropertyDescriptor.Builder().fromPropertyDescriptor(descriptor).dynamic(true).build();
        }

        if (descriptor == null) {
            descriptor = new PropertyDescriptor.Builder().fromPropertyDescriptor(specDescriptor).addValidator(Validator.INVALID).dynamic(true).build();
        }
        return descriptor;
    }

    private PropertyDescriptor getSupportedPropertyDescriptor(final PropertyDescriptor specDescriptor) {
        final List<PropertyDescriptor> supportedDescriptors = getSupportedPropertyDescriptors();
        if (supportedDescriptors != null) {
            for (final PropertyDescriptor desc : supportedDescriptors) { //find actual descriptor
                if (specDescriptor.equals(desc)) {
                    return desc;
                }
            }
        }

        return null;
    }

    /**
     * Allows subclasses to register which property descriptor objects are
     * supported. Default return is an empty set.
     *
     * @return PropertyDescriptor objects this processor currently supports
     */
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return Collections.emptyList();
    }

    /**
     * <p>
     * Used to allow subclasses to determine what PropertyDescriptor if any to
     * use when a property is requested for which a descriptor is not already
     * registered. By default this method simply returns a null descriptor. By
     * overriding this method processor implementations can support dynamic
     * properties since this allows them to register properties on demand. It is
     * acceptable for a dynamically generated property to indicate it is
     * required so long as it is understood it is only required once set.
     * Dynamic properties by definition cannot be required until used.</p>
     *
     * <p>
     * This method should be side effect free in the subclasses in terms of how
     * often it is called for a given property name because there is guarantees
     * how often it will be called for a given property name.</p>
     *
     * <p>
     * Default is null.
     *
     * @param propertyDescriptorName used to lookup if any property descriptors exist for that name
     * @return new property descriptor if supported
     */
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(final String propertyDescriptorName) {
        return null;
    }
}
```

`AbstractConfigurableComponent` 提供了 `#getSupportedPropertyDescriptors()` 和 `#getSupportedDynamicPropertyDescriptor(String)` 方法用于子类继承，提供组件需要的参数。

```java
public abstract class AbstractConfigurableComponent implements ConfigurableComponent {

    @Override
    public final Collection<ValidationResult> validate(final ValidationContext context) {
        // goes through context properties, should match supported properties + supported dynamic properties
        final Collection<ValidationResult> results = new ArrayList<>();
        final Set<PropertyDescriptor> contextDescriptors = context.getProperties().keySet();

        for (final PropertyDescriptor descriptor : contextDescriptors) {
            // If the property descriptor's dependency is not satisfied, the property does not need to be considered, as it's not relevant to the
            // component's functionality.
            final boolean dependencySatisfied = context.isDependencySatisfied(descriptor, this::getPropertyDescriptor);
            if (!dependencySatisfied) {
                continue;
            }

            validateDependencies(descriptor, context, results);

            String value = context.getProperty(descriptor).getValue();
            if (value == null) {
                value = descriptor.getDefaultValue();
            }

            if (value == null && descriptor.isRequired()) {
                String displayName = descriptor.getDisplayName();
                ValidationResult.Builder builder = new ValidationResult.Builder().valid(false).input(null).subject(displayName != null ? displayName : descriptor.getName());
                builder = (displayName != null) ? builder.explanation(displayName + " is required") : builder.explanation(descriptor.getName() + " is required");
                results.add(builder.build());
                continue;
            } else if (value == null) {
                continue;
            }

            final ValidationResult result = descriptor.validate(value, context);
            if (!result.isValid()) {
                results.add(result);
            }
        }

        // only run customValidate if regular validation is successful. This allows Processor developers to not have to check
        // if values are null or invalid so that they can focus only on the interaction between the properties, etc.
        if (results.isEmpty()) {
            final Collection<ValidationResult> customResults = customValidate(context);
            if (null != customResults) {
                for (final ValidationResult result : customResults) {
                    if (!result.isValid()) {
                        results.add(result);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Allows subclasses to perform their own validation on the already set
     * properties. Since each property is validated as it is set this allows
     * validation of groups of properties together. Default return is an empty
     * set.
     *
     * This method will be called only when it has been determined that all
     * property values are valid according to their corresponding
     * PropertyDescriptor's validators.
     *
     * @param validationContext provides a mechanism for obtaining externally
     * managed values, such as property values and supplies convenience methods
     * for operating on those values
     *
     * @return Collection of ValidationResult objects that will be added to any
     * other validation findings - may be null
     */
    protected Collection<ValidationResult> customValidate(final ValidationContext validationContext) {
        return Collections.emptySet();
    }

    private void validateDependencies(final PropertyDescriptor descriptor, final ValidationContext context, final Collection<ValidationResult> results) {
        // Ensure that we don't have any dependencies on non-existent properties.
        final Set<PropertyDependency> dependencies = descriptor.getDependencies();
        for (final PropertyDependency dependency : dependencies) {
            final String dependentPropertyName = dependency.getPropertyName();

            // If there's a supported property descriptor then all is okay.
            final PropertyDescriptor specDescriptor = new PropertyDescriptor.Builder().name(dependentPropertyName).build();
            final PropertyDescriptor supportedDescriptor = getSupportedPropertyDescriptor(specDescriptor);
            if (supportedDescriptor != null) {
                continue;
            }

            final PropertyDescriptor dynamicPropertyDescriptor = getSupportedDynamicPropertyDescriptor(dependentPropertyName);
            if (dynamicPropertyDescriptor == null) {
                results.add(new ValidationResult.Builder()
                    .subject(descriptor.getDisplayName())
                    .valid(false)
                    .explanation("Property depends on property " + dependentPropertyName + ", which is not a known property")
                    .build());
            }

            // Dependent property is supported as a dynamic property. This is okay as long as there is a value set.
            final PropertyValue value = context.getProperty(dynamicPropertyDescriptor);
            if (value == null || !value.isSet()) {
                results.add(new ValidationResult.Builder()
                    .subject(descriptor.getDisplayName())
                    .valid(false)
                    .explanation("Property depends on property " + dependentPropertyName + ", which is not a known property")
                    .build());
            }
        }

    }
}
```

同样 `AbstractConfigurableComponent` 提供了 `#customValidate(ValidationContext)` 方法用于子类进行属性校验。

