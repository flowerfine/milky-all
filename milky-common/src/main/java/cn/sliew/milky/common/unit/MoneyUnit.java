package cn.sliew.milky.common.unit;

import java.math.BigDecimal;

public enum MoneyUnit {

    HAO {
        @Override
        public BigDecimal toHao(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toFen(BigDecimal size) {
            return size.divide(C1.divide(C0));
        }

        @Override
        public BigDecimal toJiao(BigDecimal size) {
            return size.divide(C2.divide(C0));
        }

        @Override
        public BigDecimal toYuan(BigDecimal size) {
            return size.divide(C3.divide(C0));
        }

        @Override
        public String getSuffix() {
            return "毫";
        }
    },

    FEN {

        @Override
        public BigDecimal toHao(BigDecimal size) {
            return size.multiply(C1.divide(C0));
        }

        @Override
        public BigDecimal toFen(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toJiao(BigDecimal size) {
            return size.divide(C2.divide(C1));
        }

        @Override
        public BigDecimal toYuan(BigDecimal size) {
            return size.divide(C3.divide(C1));
        }

        @Override
        public String getSuffix() {
            return "分";
        }
    },

    JIAO {
        @Override
        public BigDecimal toHao(BigDecimal size) {
            return size.multiply(C2.divide(C0));
        }

        @Override
        public BigDecimal toFen(BigDecimal size) {
            return size.multiply(C2.divide(C1));
        }

        @Override
        public BigDecimal toJiao(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toYuan(BigDecimal size) {
            return size.divide(C3.divide(C2));
        }

        @Override
        public String getSuffix() {
            return "角";
        }
    },

    YUAN {
        @Override
        public BigDecimal toHao(BigDecimal size) {
            return size.multiply(C3.divide(C0));
        }

        @Override
        public BigDecimal toFen(BigDecimal size) {
            return size.multiply(C3.divide(C1));
        }

        @Override
        public BigDecimal toJiao(BigDecimal size) {
            return size.multiply(C3.divide(C2));
        }

        @Override
        public BigDecimal toYuan(BigDecimal size) {
            return size;
        }

        @Override
        public String getSuffix() {
            return "元";
        }
    };

    static final BigDecimal _TEN = BigDecimal.TEN;
    static final BigDecimal _HUNDRED = BigDecimal.valueOf(100L);
    static final BigDecimal C0 = BigDecimal.ONE;
    static final BigDecimal C1 = C0.multiply(_HUNDRED);
    static final BigDecimal C2 = C1.multiply(_TEN);
    static final BigDecimal C3 = C2.multiply(_TEN);

    public abstract BigDecimal toHao(BigDecimal size);

    public abstract BigDecimal toFen(BigDecimal size);

    public abstract BigDecimal toJiao(BigDecimal size);

    public abstract BigDecimal toYuan(BigDecimal size);

    public abstract String getSuffix();
}
