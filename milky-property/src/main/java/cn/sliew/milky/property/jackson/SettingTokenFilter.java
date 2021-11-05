package cn.sliew.milky.property.jackson;

import com.fasterxml.jackson.core.filter.TokenFilter;

import java.util.function.Predicate;

public class SettingTokenFilter extends TokenFilter {

    private Predicate<String> predicate;

    public SettingTokenFilter(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public TokenFilter includeProperty(String name) {
        if (predicate.test(name)) {
            return TokenFilter.INCLUDE_ALL;
        }
        return null;
    }
}
