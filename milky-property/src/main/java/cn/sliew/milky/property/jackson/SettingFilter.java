package cn.sliew.milky.property.jackson;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.function.Predicate;

public class SettingFilter extends SimpleBeanPropertyFilter {

    private Predicate<String> predicate;

    public SettingFilter(Predicate<String> predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    protected boolean include(BeanPropertyWriter writer) {
        return super.include(writer) && predicate.negate().test(writer.getName());
    }

    @Override
    protected boolean include(PropertyWriter writer) {
        return super.include(writer) && predicate.negate().test(writer.getName());
    }
}
