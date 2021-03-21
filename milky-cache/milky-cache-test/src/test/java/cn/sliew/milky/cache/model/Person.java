package cn.sliew.milky.cache.model;

import java.util.Arrays;
import java.util.Objects;

public class Person {

    private String name = "name";

    private int age = 18;

    private byte deleted = 1;

    private String[] value = {"value1", "value2"};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte getDeleted() {
        return deleted;
    }

    public void setDeleted(byte deleted) {
        this.deleted = deleted;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age &&
                deleted == person.deleted &&
                Objects.equals(name, person.name) &&
                Arrays.equals(value, person.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, age, deleted);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", deleted=" + deleted +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}