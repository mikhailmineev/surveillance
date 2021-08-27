package ru.mm.surv.codecs.webm.incubator.streamm;

import ru.mm.surv.codecs.webm.event.EventImpl;

import java.util.Date;

public class ServerStatusEvent extends EventImpl {

    public static final int DEFAULT_TYPE = 1;

    private int clientCount = -1;
    private int clientLimit = -1;

    public ServerStatusEvent() {
        super(DEFAULT_TYPE, new Date());
    }

    public void setClientCount(int newClientCount) {
        clientCount = newClientCount;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(40);
        sb.append("{cls:4, clientCount:");
        sb.append(clientCount);
        sb.append(", clientLimit:");
        sb.append(clientLimit);
        sb.append(",time:");
        sb.append(getDate().getTime());
        sb.append("},");
        return sb.toString();
    }
}
