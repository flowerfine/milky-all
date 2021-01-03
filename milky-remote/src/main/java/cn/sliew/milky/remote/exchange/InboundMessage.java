package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.common.release.Releasable;

import java.util.Map;

public class InboundMessage implements Releasable {

    private final Map<String, String> header;
    private final byte[] content;
    private final Exception exception;
    private final boolean isPing;

    public InboundMessage(Map<String, String> header, byte[] content, Releasable breakerRelease) {
        this.header = header;
        this.content = content;
        this.exception = null;
        this.isPing = false;
    }

    @Override
    public void close() {

    }
}
