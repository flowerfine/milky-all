package cn.sliew.milky.common.component;

import cn.sliew.milky.common.component.fsm.LifecycleState;
import cn.sliew.milky.common.release.Releasable;

public interface LifecycleComponent extends Releasable {

    LifecycleState lifecycleState();

    void addLifecycleListener(LifecycleListener listener);

    void removeLifecycleListener(LifecycleListener listener);

    void start();

    void stop();
}