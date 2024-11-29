package cn.sliew.milky.common.unit;

import cn.sliew.milky.common.primitives.Strings;

import java.math.BigDecimal;
import java.util.Locale;

public class MoneyValue implements Comparable<MoneyValue> {

    public static final MoneyValue ZERO = new MoneyValue(BigDecimal.ZERO, MoneyUnit.HAO);

    private final BigDecimal value;
    private final MoneyUnit unit;

    public MoneyValue(BigDecimal hao) {
        this(hao, MoneyUnit.HAO);
    }

    public MoneyValue(BigDecimal value, MoneyUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getHao() {
        return unit.toHao(value);
    }

    public BigDecimal getFen() {
        return unit.toFen(value);
    }

    public BigDecimal getJiao() {
        return unit.toJiao(value);
    }

    public BigDecimal getYuan() {
        return unit.toYuan(value);
    }

    public BigDecimal getFenFrac() {
        return getHao().divide(MoneyUnit.C1);
    }

    public BigDecimal getJiaoFrac() {
        return getHao().divide(MoneyUnit.C2);
    }

    public BigDecimal getYuanFrac() {
        return getHao().divide(MoneyUnit.C3);
    }

    public static MoneyValue parseMoneyValue(String value) throws ValueParseException {
        return parseMoneyValue(value, null);
    }

    public static MoneyValue parseMoneyValue(String value, MoneyValue defaultValue) throws ValueParseException {
        if (value == null) {
            return defaultValue;
        }
        String lowerValue = value.toLowerCase(Locale.ROOT).trim();
        if (lowerValue.endsWith("毫")) {
            return parse(value, lowerValue, "毫", MoneyUnit.HAO);
        } else if (lowerValue.endsWith("分")) {
            return parse(value, lowerValue, "分", MoneyUnit.FEN);
        } else if (lowerValue.endsWith("角")) {
            return parse(value, lowerValue, "角", MoneyUnit.JIAO);
        } else if (lowerValue.endsWith("元")) {
            return parse(value, lowerValue, "元", MoneyUnit.YUAN);
        } else if (lowerValue.equals("0")) {
            // Allow this special value to be unit-less:
            return new MoneyValue(BigDecimal.ZERO, MoneyUnit.HAO);
        } else {
            // Missing units:
            throw new ValueParseException(String.format("failed to parse value [{}] as money: unit is missing or unrecognized", value));
        }
    }

    private static MoneyValue parse(final String initialInput, final String normalized, final String suffix, MoneyUnit unit) {
        final String s = normalized.substring(0, normalized.length() - suffix.length()).trim();
        try {
            return new MoneyValue(BigDecimal.valueOf(Long.parseLong(s)), unit);
        } catch (NumberFormatException e) {
            throw new ValueParseException(String.format("failed to parse value [{}] as money", initialInput), e);
        }
    }

    @Override
    public int compareTo(MoneyValue other) {
        BigDecimal thisValue = value.multiply(unit.toHao(BigDecimal.ONE));
        BigDecimal otherValue = other.value.multiply(other.unit.toHao(BigDecimal.ONE));
        return thisValue.compareTo(otherValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return compareTo((MoneyValue) o) == 0;
    }

    @Override
    public int hashCode() {
        return value.multiply(unit.toHao(BigDecimal.ONE)).hashCode();
    }

    @Override
    public String toString() {
        BigDecimal hao = getHao();
        BigDecimal value = hao;
        String suffix = MoneyUnit.HAO.getSuffix();
        if (value.compareTo(MoneyUnit.C3) >= 0) {
            value = getYuanFrac();
            suffix = MoneyUnit.YUAN.getSuffix();
        } else if (value.compareTo(MoneyUnit.C2) >= 0) {
            value = getJiaoFrac();
            suffix = MoneyUnit.JIAO.getSuffix();
        } else if (value.compareTo(MoneyUnit.C1) >= 0) {
            value = getFenFrac();
            suffix = MoneyUnit.FEN.getSuffix();
        }
        return Strings.format1Decimals(value.doubleValue(), suffix);
    }
}
