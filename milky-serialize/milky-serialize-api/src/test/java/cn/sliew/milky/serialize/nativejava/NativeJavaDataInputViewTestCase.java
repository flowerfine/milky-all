package cn.sliew.milky.serialize.nativejava;

import cn.sliew.milky.test.MilkyTestCase;
import cn.sliew.milky.test.extension.random.RandomizedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeJavaDataInputViewTestCase extends MilkyTestCase {

    private File temp;

    @BeforeEach
    private void before(@TempDir Path path) {
        temp = path.resolve("temp.txt").toFile();
    }

    @Test
    public void testWithFile() throws IOException {
        Random random = RandomizedContext.current().getRandomness().getRandom();
        FileOutputStream outputStream = new FileOutputStream(temp);
        NativeJavaDataOutputView outputView = new NativeJavaDataOutputView(outputStream);
        boolean v = random.nextBoolean();
        outputView.writeBoolean(v);
        outputView.flushBuffer();

        FileInputStream inputStream = new FileInputStream(temp);
        NativeJavaDataInputView inputView = new NativeJavaDataInputView(inputStream);
        assertEquals(v, inputView.readBoolean());
    }

    @Test
    public void testWriteReadObject() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NativeJavaDataOutputView outputView = new NativeJavaDataOutputView(outputStream);
        outputView.writeByte(1);
        outputView.flushBuffer();
        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        NativeJavaDataInputView inputView = new NativeJavaDataInputView(inputStream);
        System.out.println(inputView.readByte());
    }
}
