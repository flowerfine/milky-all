package cn.sliew.milky.event.base;

class EchoEventListener extends AbstractEventListener<EchoEvent> {

    @Override
    protected void handleEvent(EchoEvent event) {
        println(this.getClass().getSimpleName() + " : " + event);
    }
}
