# ThrowableCollector Component
在开发中，异常处理不可避免，设计异常、处理异常的能力直接影响应用的质量好坏。混杂的异常处理代码会影响代码阅读体验，把开发者的注意力从理解代码意图吸引到异常上去。**异常处理很重要，但是不能喧宾夺主，搞乱代码逻辑**。

## 顶层代码使用 `try-catch-finally` 作为最终兜底。

面向对象编程推崇封装，API根据处理的功能分为应用底层和用户高级两个层级。大胆地在应用底层代码中抛出异常，中断执行，而在用户高级代码中添加`try-catch-finally`，保证不会遗漏异常情况的处理。

```java
try {
    // do something
} catch(Exception e) {
    // handle exception
} finally {
    // release resource etc.
}
```

对于函数调用者而言，总是使用 `try-catch-finally` 可以保证写出健壮的代码，如果再加上良好的异常处理层次就可以保证代码的可读性。

## 如何优雅地处理受检查异常。

`java` 中异常体系受诟病的一点就是**受检查异常**。它强制在类的继承体系和客户端代码中处理异常，破坏封装。

```java
public void doSomething() throws Exception;
```

我一般推崇的是尽量在上层代码中做异常的最终处理，多数情况下都是继续往外抛异常而不处理。但是 `try-catch` 语句中不能直接抛出被捕获的异常，需要包一层新的异常，而这会造成上层代码在收到异常时异常层级过多，日志体验极差。

这里介绍一个小技巧，轻松抛出 `catch` 语句抛出的异常。

```java
public static <T extends Throwable> void throwAs(Throwable t) throws T {
    throw (T) t;
}

// example
try {
    // do something may throw exception.
} catch (Exception e) {
    throwAs(e);
    // may need add return statement, but never invoked.
    return null;
}
```

## Lambda中的异常处理

在 `java 1.8` 中一个重大的特性是引入了函数式编程，函数式编程在多种语言中被证明能够极大增加代码的生产力。但是`java`的函数式编程少了重要的特性：如何在lambda中处理受检查的异常？

```java

list.forEach((input) -> {
    try {
        doSomething(input);
    } catch (Exception e) {
        // handle exception
    }
});

private void doSomething(String input) throws Exception {
    // do something
}
```

如上代码，原本是纵享丝滑的函数式编程却因为调用的函数中声明了一个受检查异常而使函数代码变得膨胀，变得不再简洁，开始变得臃肿复杂。

解决思路可以是定义支持异常声明的函数如：

```java
/**
 * A {@link Consumer}-like interface which allows throwing checked exceptions.
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
```

然而这是牺牲了在函数中处理异常的功能，将异常的处理的责任强行推倒了函数调用者身上。

还可以定义一个专门处理可能会抛出异常的代码执行组件，也是本文要介绍的组件：

```java
/**
 * Component that can collect one Throwable instance.
 */
public class ThrowableCollector<T> {

    /**
     * {@link Throwable} holder.
     */
    private volatile Optional<Throwable> throwableHolder = Optional.empty();

    /**
     * prevent construct directly, suggest to static factory method {@link ThrowableCollector#create()}.
     */
    private ThrowableCollector() {

    }

    /**
     * Execute the supplied {@link Executable} and collect any {@link Throwable}
     * thrown during the execution.
     *
     * <p>If the {@code Executable} throws an <em>unrecoverable</em> exception
     * &mdash; for example, an {@link OutOfMemoryError} &mdash; this method will
     * rethrow it.
     *
     * @param executable the {@code Executable} to execute
     */
    public void execute(Executable executable) {
        execute(() -> {
            executable.execute();
            return null;
        });
    }

    public Optional<T> execute(ExecutableWithResult<T> executable) {
        try {
            return Optional.ofNullable(executable.execute());
        } catch (Throwable t) {
            ThrowableUtil.rethrowIfUnrecoverable(t);
            add(t);
            return Optional.empty();
        }
    }

    /**
     * Add the supplied {@link Throwable} to this {@code ThrowableCollector}.
     *
     * @param t the {@code Throwable} to add
     * @see #execute(Executable)
     */
    private void add(Throwable t) {
        checkNotNull(t, () -> "Throwable must not be null");

        if (throwableHolder.isPresent()) {
            throwableHolder.get().addSuppressed(t);
        } else {
            throwableHolder = Optional.of(t);
        }
    }

    /**
     * Get the first {@link Throwable} collected by this {@code ThrowableCollector}.
     *
     * <p>If this collector is not empty, the first collected {@code Throwable}
     * will be returned with any additional {@code Throwables}
     * {@linkplain Throwable#addSuppressed(Throwable) suppressed} in the
     * first {@code Throwable}.
     *
     * @return the first collected {@code Throwable} or {@code null} if this
     * {@code ThrowableCollector} is empty
     */
    public Optional<Throwable> getThrowable() {
        return throwableHolder;
    }

    /**
     * Functional interface for an executable block of code that may throw a {@link Throwable}.
     */
    @FunctionalInterface
    public interface Executable {

        /**
         * Execute this executable, potentially throwing a {@link Throwable}
         * that signals abortion or failure.
         */
        void execute() throws Throwable;

    }

    /**
     * Functional interface for an executable block of code that may throw a {@link Throwable}.
     *
     * @param <T> result type
     */
    @FunctionalInterface
    public interface ExecutableWithResult<T> {

        /**
         * Execute this executable, potentially throwing a {@link Throwable}
         * that signals abortion or failure.
         *
         * @return executable result
         * @throws Throwable
         */
        T execute() throws Throwable;

    }

    /**
     * Factory method for {@code ThrowableCollector} instances creation.
     *
     * @return throwable collector
     */
    public static <T> ThrowableCollector<T> create() {
        return new ThrowableCollector();
    }

}
```

它有两个好处，一个是自动的 `try-catch-finally` 处理，另一个是支持函数式调用，使代码简洁而优雅。案例如下：

```java
ThrowableCollector throwableCollector = ThrowableCollector.create();
throwableCollector.execute(() -> doSomething());
throwableCollector.getThrowable().ifPresent(throwable -> handleException(throwable));
```

## 特例模式的运用

但是，这里还有一个瑕疵，异常的存在破坏了函数式的纯洁性，**函数返回的异常破坏了结果的类型一致性**。

《整洁代码之道》中异常处理的章节中给出了一个案例：

```java
try {
    MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
    m_total += expenses.getTotal();
} catch (MealExpensesNotFound e) {
    m_total = getMealPerDiem();
}
```

异常中断了业务逻辑，使代码跳转到了 `catch` 语句，而在异常处理的地方添加了兜底手段。这种写法违反了一个开发原则：**禁止使用异常作为流程控制手段**。即使有人反驳说这是一种特殊的异常处理，它保证了程序的健壮性，但是已经发生的线上故障经历明确地指出：当大量异常抛出时，这种 `try-catch` 的跳转会很快耗尽服务器的CPU资源。

那么有什么方式能够解决这个问题吗？假如`ExpenseReportDAO`类总能返回一个`MealExpenses`对象就可以解决。而在没有餐食消耗时，`ExpenseReportDAO`返回一个餐食补贴的`MealExpenses`对象。

```java
public class PerDiemMealExpenses implements MealExpenses {
    public int getTotal() {
        // return the per diem default
    }
}
```

这种写法叫做特例模式。创建一个类或配置一个对象，用来处理特例。被调用者处理了特例就可以避免客户端代码应付异常行为，异常行为被封装到特例对象中。

## Scala异常处理的支持

在`Scala`中函数是第一等的公民，`Scala`是支持函数式编程范式和面向对象编程范式的混合。`Scala`对函数式的异常处理提供了语言级的支持。

```scala
case class Person(name: String, location: String)
val person = Person("Akhil", "Delhi")

def getPerson(person: Person) : Person = {
  if (person.location.equals("Delhi")) {
    person
  } else {
    throw new Exception("Invalid Person Object")
  }
}

val requiredPerson = getPerson(person)
```

### Option

`Option` 可以同时有结果返回和无结果返回的场景。`Option` 有两个子类 `Some` 和 `None`，分别代表有结果返回和无结果。

```scala
case class Person(name: String, location: String)

val person = Person("Akhil", "Delhi")
def getPerson(person: Person): Option[Person] = {

  if (person.location.equals("Delhi")) {
    Some(person)
  } else {
    None
  }
}

val requiredPerson: Option[Person] = getPerson(person)
```

## Try

`Try `可以用来处理代码调用，它的返回结果有 `Success(value)` 和 `Failure(exception)`。

```scala
import scala.util.Try

case class Person(name: String, location: String)

val person = Person("Akhil", "Delhi")
def getPerson(person: Person): Try[Person] = {

  Try {
    if (person.location.equals("Delhi")) {
      person
    } else {
      throw new Exception("Invalid Person Object")
    }
  }
}

val requiredPerson: Try[Persion] = getPerson(person)
```

## Either

`Either` 的使用方式比 `Option` 和 `Try` 更合理。`Option` 的缺点很明显，不能返回异常信息，只能表达有结果和 `null` 两种场景，用 `null` 来暗示可能有异常的发生，但是有些业务场景中 `null` 可能是正常的返回结果。`Try` 可以为客户端代码提供异常信息，但是客户端代码依然要抛出这个异常，而这是我们一直想要避免的。而 `Either` 则可以解决 `Option` 和 `Try` 的不足。它返回 `Left` 和 `Right`，`Left` 代表异常信息，`Right` 代表返回结果。

```scala
case class Person(name: String, location: String)
case class ErrorMessage(message: String)

val person = Person("Akhil", "Delhi")
def getPerson(person: Person): Either[ErrorMessage, Person] = {

  if(person.location.equals("Delhi")) {
    Right(person)
  }
  else {
    Left(ErrorMessage("Invalid Person Object"))
  }
}

val requiredPerson: Either[ErrorMessage, Person] = getPerson(person)
```

## Java函数编程库对异常处理的类似支持

`java` 中函数编程功能的支持还很弱，赶不上 `scala` 这样的函数式编程语言。但是 `vavr` 提供了类似的功能用于实现 `scala` 中的 `Option`、`Try` 和 `Either`。

* `Option`。[vavr_option](https://docs.vavr.io/#_option)。
* `Try`。[vavr_try](https://docs.vavr.io/#_try)。
* `Either`。[vavr_either](https://docs.vavr.io/#_either)。

## 如何选择

实现很简单，思想最重要。无论是我的开源组件库提供的`ThrowableCollector`还是`scala`或`vavr`提供的特例模式实现，代码实现都是很简单的，如果不想在项目中引入额外的依赖，可以自己在应用中实现。也可以使用`try-catch-finally`或者被调用者处理特例封装异常到特例对象中都可以的，一切自由选择。