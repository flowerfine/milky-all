package cn.sliew.milky.common.component.fsm;

import cn.sliew.milky.common.state.StateEvent;
import cn.sliew.milky.common.state.StateMachine;

public enum InitializedState implements LifecycleState {

    INSTANCE;

    @Override
    public void entryAction() {

    }

    @Override
    public void exitAction() {

    }

    @Override
    public boolean processEvent(StateMachine fsm, StateEvent event) {
        return false;
    }

}
