package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.serialize.protostuff.ProtostuffDataInputView;
import cn.sliew.milky.serialize.protostuff.ProtostuffDataOutputView;
import org.caffinitas.ohc.CacheSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ProtostuffCacheSerializer<T> implements CacheSerializer<T> {

    private static final Logger log = LoggerFactory.getLogger(ProtostuffCacheSerializer.class);

    public static final ProtostuffCacheSerializer INSTANCE = new ProtostuffCacheSerializer();

    /**
     * todo 这里其实有一个额外的copy的动作，后续可以考虑优化为直接写入ByteBuffer
     */
    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ProtostuffDataOutputView outputView = new ProtostuffDataOutputView(outputStream);
            outputView.writeObject(t);
            outputView.flushBuffer();
            byte[] bytes = outputStream.toByteArray();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        try {
            byte[] bytes = new byte[byteBuffer.getInt()];
            byteBuffer.get(bytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ProtostuffDataInputView inputView = new ProtostuffDataInputView(inputStream);
            return (T) inputView.readObject();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int serializedSize(T t) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ProtostuffDataOutputView outputView = new ProtostuffDataOutputView(outputStream);
            outputView.writeObject(t);
            outputView.flushBuffer();
            byte[] bytes = outputStream.toByteArray();
            return bytes.length + 4;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }
}
