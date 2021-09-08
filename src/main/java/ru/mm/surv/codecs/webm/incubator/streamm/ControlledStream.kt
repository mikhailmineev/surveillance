package ru.mm.surv.codecs.webm.incubator.streamm;

public class ControlledStream extends Stream {

    private final int maxClients;
    private int numClients;

    public ControlledStream(int maxClients) {
        this.maxClients = maxClients;
    }

    public boolean subscribe(boolean force) {
        if (force || numClients < maxClients) {
            numClients++;
            refresStatus();
            return true;
        }
        return false;
    }

    public void unsubscribe() {
        numClients--;
        refresStatus();
    }

    protected void refresStatus() {
        ServerStatusEvent event = new ServerStatusEvent();
        event.setClientCount(numClients);
        postEvent(event);
    }
}
