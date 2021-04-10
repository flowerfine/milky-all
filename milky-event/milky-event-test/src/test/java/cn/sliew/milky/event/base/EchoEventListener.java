package cn.sliew.milky.event.base;

public class EchoEventListener extends AbstractEventListener<EchoEvent> {

    @Override
    protected void handleEvent(EchoEvent event) {
        println(this.getClass().getSimpleName() + " : " + event);
    }
}
