package cn.sliew.milky.serialize.protostuff;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtostuffDataTest extends MilkyTestCase {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ProtostuffDataOutputView outputView = new ProtostuffDataOutputView(outputStream);
        Person person = new Person("123", "12aljeiaoe", 23, "aeioanleinblaukefhalreuafe", "12390u4qry2@qq.com");
        outputView.writeObject(person);
        outputView.flushBuffer();
        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ProtostuffDataInputView inputView = new ProtostuffDataInputView(inputStream);
        System.out.println(inputView.readObject());
    }

    public static class Person {
        private String id;

        private String name;

        private Integer age;

        private String address;

        private String email;

        public Person(String id, String name, Integer age, String address, String email) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.address = address;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", address='" + address + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
