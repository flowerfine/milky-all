package cn.sliew.milky.common.unit;

import java.math.BigDecimal;

public enum NumberUnit {

    ONE {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size.divide(C1.divide(C0));
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size.divide(C2.divide(C0));
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size.divide(C3.divide(C0));
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size.divide(C4.divide(C0));
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size.divide(C5.divide(C0));
        }

        @Override
        public String getSuffix() {
            return "";
        }
    },
    TEN {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size.multiply(C1.divide(C0));
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size.divide(C2.divide(C1));
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size.divide(C3.divide(C1));
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size.divide(C4.divide(C1));
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size.divide(C5.divide(C1));
        }

        @Override
        public String getSuffix() {
            return "十";
        }
    },
    HUNDRED {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size.multiply(C2.divide(C0));
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size.multiply(C2.divide(C1));
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size.divide(C3.divide(C2));
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size.divide(C4.divide(C2));
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size.divide(C5.divide(C2));
        }

        @Override
        public String getSuffix() {
            return "百";
        }
    },
    THOUSAND {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size.multiply(C3.divide(C0));
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size.multiply(C3.divide(C1));
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size.multiply(C3.divide(C2));
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size.divide(C4.divide(C3));
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size.divide(C5.divide(C3));
        }

        @Override
        public String getSuffix() {
            return "千";
        }
    },
    TEN_THOUSAND {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size.multiply(C4.divide(C0));
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size.multiply(C4.divide(C1));
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size.multiply(C4.divide(C2));
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size.multiply(C4.divide(C3));
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size;
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size.divide(C5.divide(C4));
        }

        @Override
        public String getSuffix() {
            return "万";
        }
    },
    TEN_MILLION {
        @Override
        public BigDecimal toOne(BigDecimal size) {
            return size.multiply(C5.divide(C0));
        }

        @Override
        public BigDecimal toTen(BigDecimal size) {
            return size.multiply(C5.divide(C1));
        }

        @Override
        public BigDecimal toHundred(BigDecimal size) {
            return size.multiply(C5.divide(C2));
        }

        @Override
        public BigDecimal toThousand(BigDecimal size) {
            return size.multiply(C5.divide(C3));
        }

        @Override
        public BigDecimal toTenThousand(BigDecimal size) {
            return size.multiply(C5.divide(C4));
        }

        @Override
        public BigDecimal toTenMillion(BigDecimal size) {
            return size;
        }

        @Override
        public String getSuffix() {
            return "亿";
        }
    };

    static final BigDecimal _TEN = BigDecimal.valueOf(10);
    static final BigDecimal C0 = BigDecimal.ONE;
    static final BigDecimal C1 = C0.multiply(_TEN);
    static final BigDecimal C2 = C1.multiply(_TEN);
    static final BigDecimal C3 = C2.multiply(_TEN);
    static final BigDecimal C4 = C3.multiply(_TEN);
    static final BigDecimal C5 = C4.multiply(BigDecimal.valueOf(10000L));

    public abstract BigDecimal toOne(BigDecimal size);

    public abstract BigDecimal toTen(BigDecimal size);

    public abstract BigDecimal toHundred(BigDecimal size);

    public abstract BigDecimal toThousand(BigDecimal size);

    public abstract BigDecimal toTenThousand(BigDecimal size);

    public abstract BigDecimal toTenMillion(BigDecimal size);

    public abstract String getSuffix();
}
