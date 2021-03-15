package cn.sliew.milky.serialize.nativejava;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeJavaDataInputViewTestCase extends MilkyTestCase {

    private File temp;

    @BeforeEach
    private void before(@TempDir Path path) {
        temp = path.resolve("temp.txt").toFile();
    }



    @Test
    public void testWithFile() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(temp);
        NativeJavaDataOutputView outputView = new NativeJavaDataOutputView(outputStream);
        outputView.writeByte(1);
        outputView.flushBuffer();

        FileInputStream inputStream = new FileInputStream(temp);
        NativeJavaDataInputView inputView = new NativeJavaDataInputView(inputStream);
        assertEquals(1, inputView.readByte());
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
