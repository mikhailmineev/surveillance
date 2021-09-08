package ru.mm.surv.codecs.webm.util.stream;

import ru.mm.surv.codecs.webm.event.EventImpl;

public class TransferEvent extends EventImpl {

    public static final int STREAM_INPUT = 1;
    public static final int STREAM_OUTPUT = 2;

    public TransferEvent(int type) {
        super(type);
    }
}
