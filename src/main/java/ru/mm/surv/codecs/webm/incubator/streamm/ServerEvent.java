package ru.mm.surv.codecs.webm.incubator.streamm;

import ru.mm.surv.codecs.webm.event.EventImpl;

import java.util.Date;

public class ServerEvent extends EventImpl {

    public static final int INPUT_START = 1;
    public static final int INPUT_STOP = 2;
    public static final int INPUT_FRAGMENT_START = 3;
    public static final int INPUT_FIRST_FRAGMENT = 5;

    public static final int CLIET_START = 1001;
    public static final int CLIET_STOP = 1002;
    public static final int CLIET_FRAGMENT_SKIP = 1005;

    private final Date startDate;

    public ServerEvent(int type) {
        this(type, null);
    }

    public ServerEvent(int type, Date startDate) {
        super(type);
        this.startDate = startDate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("{cls:1, type:");
        sb.append(getType());
        if (startDate != null) {
            sb.append(",start:");
            sb.append(startDate.getTime());
        }
        sb.append(",time:");
        sb.append(getDate().getTime());
        sb.append("},");
        return sb.toString();
    }
}
