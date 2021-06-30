package cn.sliew.milky.dsl;

public class Composite {

    private String foo;
    private String bar;
    private String subBar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getSubBar() {
        return subBar;
    }

    public void setSubBar(String subBar) {
        this.subBar = subBar;
    }

    @Override
    public String toString() {
        return "Composite{" +
                "foo='" + foo + '\'' +
                ", bar='" + bar + '\'' +
                ", subBar='" + subBar + '\'' +
                '}';
    }
}
