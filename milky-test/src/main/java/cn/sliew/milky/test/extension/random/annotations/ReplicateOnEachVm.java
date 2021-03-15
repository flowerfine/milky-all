package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

/**
 * <p>Replicates the test class on each concurrent forked JVM. If only a single JVM
 * is used for running tests, this annotation has no effect.</p>
 * 
 * <p>The purpose of this annotation is to, for example, replicate a single test suite
 * across multiple forked JVMs and then selectively ignore or execute tests on each
 * JVM, depending on its number, providing poor-man's load balancing for individual 
 * test cases (test suites are balanced by the framework itself).</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface ReplicateOnEachVm {}