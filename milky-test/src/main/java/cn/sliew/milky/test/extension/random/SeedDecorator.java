package cn.sliew.milky.test.extension.random;

/**
 * This is an advanced feature. See {@link SeedDecorators} annotation.
 */
public interface SeedDecorator {
  /**
   * Called once after instantiation to set up the decorator. 
   */
  void initialize(Class<?> suiteClass);

  /**
   * Called to decorate the initial seed for a {@link Randomness}.
   */
  long decorate(long seed);
}
