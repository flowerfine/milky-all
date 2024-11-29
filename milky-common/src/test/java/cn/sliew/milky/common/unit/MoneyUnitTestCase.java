package cn.sliew.milky.common.unit;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cn.sliew.milky.common.unit.ByteUnit.*;
import static cn.sliew.milky.common.unit.MoneyUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class MoneyUnitTestCase extends MilkyTestCase {

    @Test
    void testHao() {
        assertThat(HAO.toHao(BigDecimal.valueOf(1)), equalTo(BigDecimal.ONE));
        assertThat(HAO.toFen(BigDecimal.valueOf(100)), equalTo(BigDecimal.ONE));
        assertThat(HAO.toJiao(BigDecimal.valueOf(1000)), equalTo(BigDecimal.ONE));
        assertThat(HAO.toYuan(BigDecimal.valueOf(10000)), equalTo(BigDecimal.ONE));
    }

    @Test
    void testFen() {
        assertThat(FEN.toHao(BigDecimal.ONE), equalTo(BigDecimal.valueOf(100L)));
        assertThat(FEN.toFen(BigDecimal.ONE), equalTo(BigDecimal.ONE));
        assertThat(FEN.toJiao(BigDecimal.TEN), equalTo(BigDecimal.ONE));
        assertThat(FEN.toYuan(BigDecimal.valueOf(100)), equalTo(BigDecimal.ONE));
    }

    @Test
    void testJiao() {
        assertThat(JIAO.toHao(BigDecimal.ONE), equalTo(BigDecimal.valueOf(1000)));
        assertThat(JIAO.toFen(BigDecimal.ONE), equalTo(BigDecimal.TEN));
        assertThat(JIAO.toJiao(BigDecimal.ONE), equalTo(BigDecimal.ONE));
        assertThat(JIAO.toYuan(BigDecimal.TEN), equalTo(BigDecimal.ONE));
    }

    @Test
    void testYuan() {
        assertThat(YUAN.toHao(BigDecimal.ONE), equalTo(BigDecimal.valueOf(10000)));
        assertThat(YUAN.toFen(BigDecimal.ONE), equalTo(BigDecimal.valueOf(100)));
        assertThat(YUAN.toJiao(BigDecimal.ONE), equalTo(BigDecimal.valueOf(10)));
        assertThat(YUAN.toYuan(BigDecimal.ONE), equalTo(BigDecimal.ONE));
    }

    @Test
    void testToString() {
        int v = randomIntBetween(1, 9);
        assertThat(new MoneyValue(YUAN.toHao(BigDecimal.valueOf(v))).toString(), equalTo(v + "元"));
        assertThat(new MoneyValue(JIAO.toHao(BigDecimal.valueOf(v))).toString(), equalTo(v + "角"));
        assertThat(new MoneyValue(FEN.toHao(BigDecimal.valueOf(v))).toString(), equalTo(v + "分"));
        assertThat(new MoneyValue(HAO.toHao(BigDecimal.valueOf(v))).toString(), equalTo(v + "毫"));
    }

}
