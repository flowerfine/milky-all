//package cn.sliew.milky.common.settings;
//
//import java.util.regex.Pattern;
//
//import static cn.sliew.milky.common.check.Ensures.checkArgument;
//
///**
// * A key that allows for static pre and suffix. This is used for settings
// * that have dynamic namespaces like for different accounts etc.
// */
//public final class AffixKey implements Key {
//    private final Pattern pattern;
//    private final String prefix;
//    private final String suffix;
//
//    AffixKey(String prefix) {
//        this(prefix, null);
//    }
//
//    AffixKey(String prefix, String suffix) {
//        checkArgument(prefix != null || suffix != null, () -> "Either prefix or suffix must be non-null");
//        assert prefix != null || suffix != null : "Either prefix or suffix must be non-null";
//
//        this.prefix = prefix;
//        if (prefix.endsWith(".") == false) {
//            throw new IllegalArgumentException("prefix must end with a '.'");
//        }
//        this.suffix = suffix;
//        if (suffix == null) {
//            pattern = Pattern.compile("(" + Pattern.quote(prefix) + "((?:[-\\w]+[.])*[-\\w]+$))");
//        } else {
//            // the last part of this regexp is to support both list and group keys
//            pattern = Pattern.compile("(" + Pattern.quote(prefix) + "([-\\w]+)\\." + Pattern.quote(suffix) + ")(?:\\..*)?");
//        }
//    }
//
//    @Override
//    public boolean match(String key) {
//        return pattern.matcher(key).matches();
//    }
//
//    /**
//     * Returns a string representation of the concrete setting key
//     */
//    String getConcreteString(String key) {
//        Matcher matcher = pattern.matcher(key);
//        if (matcher.matches() == false) {
//            throw new IllegalStateException("can't get concrete string for key " + key + " key doesn't match");
//        }
//        return matcher.group(1);
//    }
//
//    /**
//     * Returns a string representation of the concrete setting key
//     */
//    String getNamespace(String key) {
//        Matcher matcher = pattern.matcher(key);
//        if (matcher.matches() == false) {
//            throw new IllegalStateException("can't get concrete string for key " + key + " key doesn't match");
//        }
//        return matcher.group(2);
//    }
//
//    public SimpleKey toConcreteKey(String missingPart) {
//        StringBuilder key = new StringBuilder();
//        if (prefix != null) {
//            key.append(prefix);
//        }
//        key.append(missingPart);
//        if (suffix != null) {
//            key.append(".");
//            key.append(suffix);
//        }
//        return new SimpleKey(key.toString());
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        if (prefix != null) {
//            sb.append(prefix);
//        }
//        if (suffix != null) {
//            sb.append('*');
//            sb.append('.');
//            sb.append(suffix);
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        AffixKey that = (AffixKey) o;
//        return Objects.equals(prefix, that.prefix) &&
//                Objects.equals(suffix, that.suffix);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(prefix, suffix);
//    }
//}