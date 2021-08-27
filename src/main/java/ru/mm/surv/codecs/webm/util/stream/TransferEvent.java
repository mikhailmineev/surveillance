package ru.mm.surv.codecs.webm.util.stream;

import ru.mm.surv.codecs.webm.event.EventImpl;

public class TransferEvent extends EventImpl {

    public static final int STREAM_INPUT = 1;
    public static final int STREAM_OUTPUT = 2;

    private int bytes;
    private long duration;

    public TransferEvent(int type, int bytes, long duration) {
        super(type);
        this.bytes = bytes;
        this.duration = duration;
    }

    public int getBytes() {
        return bytes;
    }

    public long getDuration() {
        return duration;
    }
}
