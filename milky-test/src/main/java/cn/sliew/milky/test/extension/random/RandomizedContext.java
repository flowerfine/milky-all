package cn.sliew.milky.test.extension.random;

import cn.sliew.milky.test.extension.random.annotations.Seed;
import cn.sliew.milky.test.extension.random.annotations.SeedDecorators;
import cn.sliew.milky.test.extension.random.annotations.TestContextRandomSupplier;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 考虑使用ThreadLocal来搞定这个问题
 */
public class RandomizedContext {

    private static final ThreadLocal<RandomizedContext> LOCAL = ThreadLocal.withInitial(() -> new RandomizedContext());

    private ExtensionContext context;
    private Randomness randomness;

    private RandomSupplier randomSupplier;

    /**
     * A sequencer for affecting the initial seed in case of rapid succession of this class
     * instance creations. Not likely, but can happen two could get the same seed.
     */
    private static final AtomicLong sequencer = new AtomicLong();

    protected RandomizedContext() {

    }

    public static RandomizedContext current() {
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }

    void initializeContext(ExtensionContext context) {
        this.context = context;
        Class<?> testClass = context.getTestClass().get();
        SeedDecorator[] decArray = detectSeedDecorators(testClass);
        this.randomSupplier = determineRandomSupplier(testClass);
        long initialSeed = initializeSeed(testClass);
        this.randomness = new Randomness(initialSeed, randomSupplier, decArray);
    }

    private SeedDecorator[] detectSeedDecorators(Class<?> testClass) {
        List<SeedDecorator> decorators = new ArrayList<>();
        for (SeedDecorators decAnn : getAnnotationsFromClassHierarchy(context.getTestClass().get(), SeedDecorators.class)) {
            for (Class<? extends SeedDecorator> clazz : decAnn.value()) {
                try {
                    SeedDecorator dec = clazz.newInstance();
                    dec.initialize(testClass);
                    decorators.add(dec);
                } catch (Throwable t) {
                    throw new RuntimeException("Could not initialize suite class: "
                            + testClass.getName() + " because its @SeedDecorators contains non-instantiable: "
                            + clazz.getName(), t);
                }
            }
        }
        return decorators.toArray(new SeedDecorator[decorators.size()]);
    }

    private RandomSupplier determineRandomSupplier(Class<?> testClass) {
        List<TestContextRandomSupplier> randomImpl = getAnnotationsFromClassHierarchy(testClass, TestContextRandomSupplier.class);
        if (randomImpl.size() == 0) {
            return RandomSupplier.DEFAULT;
        } else {
            Class<? extends RandomSupplier> clazz = randomImpl.get(randomImpl.size() - 1).value();
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("Could not instantiate random supplier of class: " + clazz, e);
            }
        }
    }

    private long initializeSeed(Class<?> testClass) {
        long randomSeed = MurmurHash3.hash(sequencer.getAndIncrement() + System.nanoTime());
        if (testClass.isAnnotationPresent(Seed.class)) {
            return seedFromAnnot(testClass, randomSeed)[0];
        }
        return randomSeed;
    }

    /**
     * Get an annotated element's {@link Seed} annotation and determine if it's fixed
     * or not. If it is fixed, return the seeds. Otherwise return <code>randomSeed</code>.
     */
    private long[] seedFromAnnot(AnnotatedElement element, long randomSeed) {
        Seed seed = element.getAnnotation(Seed.class);
        String seedChain = seed.value();
        if (seedChain.equals("random")) {
            return new long[]{randomSeed};
        }

        return SeedUtils.parseSeedChain(seedChain);
    }


    /**
     * seed.
     */
    long getRunnerSeed() {
        return randomness.getSeed();
    }

    /**
     * Returns the master seed, formatted.
     */
    public String getSeedAsString() {
        return SeedUtils.formatSeed(getRunnerSeed());
    }

    /**
     * Source of randomness for the context's thread.
     */
    public Randomness getRandomness() {
        return randomness;
    }

    /**
     * Collect all annotations from a clazz hierarchy. Superclass's annotations come first.
     * {@link Inherited} annotations are removed (hopefully, the spec. isn't clear on this whether
     * the same object is returned or not for inherited annotations).
     */
    private <T extends Annotation> List<T> getAnnotationsFromClassHierarchy(Class<?> clazz, Class<T> annotation) {
        List<T> anns = new ArrayList<>();
        IdentityHashMap<T, T> inherited = new IdentityHashMap<>();
        for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
            if (c.isAnnotationPresent(annotation)) {
                T ann = c.getAnnotation(annotation);
                if (ann.annotationType().isAnnotationPresent(Inherited.class) &&
                        inherited.containsKey(ann)) {
                    continue;
                }
                anns.add(ann);
                inherited.put(ann, ann);
            }
        }

        Collections.reverse(anns);
        return anns;
    }


}
