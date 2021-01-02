package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.common.unit.TimeValue;

public enum TransportOptions {

    STATE("STATE", TimeValue.ZERO, "传输集群状态"),
    PING("PING", TimeValue.ZERO, "ping请求"),
    ;
    private String type;
    private TimeValue timeout;
    private String desc;

    TransportOptions(String type, TimeValue timeout, String desc) {
        this.type = type;
        this.timeout = timeout;
        this.desc = desc;
    }
}