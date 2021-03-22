package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.serialize.nativejava.NativeJavaDataInputView;
import cn.sliew.milky.serialize.nativejava.NativeJavaDataOutputView;
import org.caffinitas.ohc.CacheSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NativeJavaCacheSerializer<T> implements CacheSerializer<T> {

    public static final NativeJavaCacheSerializer INSTANCE = new NativeJavaCacheSerializer();

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            NativeJavaDataOutputView outputView = new NativeJavaDataOutputView(outputStream);
            outputView.writeObject(t);
            outputView.flushBuffer();
            byte[] bytes = outputStream.toByteArray();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        try {
            byte[] bytes = new byte[byteBuffer.getInt()];
            byteBuffer.get(bytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            NativeJavaDataInputView inputView = new NativeJavaDataInputView(inputStream);
            return (T) inputView.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int serializedSize(T t) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            NativeJavaDataOutputView outputView = new NativeJavaDataOutputView(outputStream);
            outputView.writeObject(t);
            outputView.flushBuffer();
            byte[] bytes = outputStream.toByteArray();
            return bytes.length + 4;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
