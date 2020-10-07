package cn.sliew.milky.common.model.param;

import java.io.Serializable;

public abstract class AbstractParam implements Serializable {

    /**
     * debug开关，用于输出接口运行细节.
     * 生产环境关闭，开发测试环境打开，手动调试、排查问题时打开.
     */
    private boolean explain = false;

    /**
     * 性能profile开关，用于输出接口性能细节.
     * 生产环境关闭，开发测试环境打开，手动调试、排查问题时打开.
     */
    private boolean profile = true;

    public boolean explain() {
        return explain;
    }

    public void setExplain(boolean explain) {
        this.explain = explain;
    }

    public boolean profile() {
        return profile;
    }

    public void setProfile(boolean profile) {
        this.profile = profile;
    }
}
