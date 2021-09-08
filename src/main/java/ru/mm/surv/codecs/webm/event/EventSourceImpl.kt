package ru.mm.surv.codecs.webm.event;

import org.jetbrains.annotations.NotNull;

public class EventSourceImpl implements EventSource {

    @Override
    public boolean postEvent(@NotNull Event event) {
        return false;

    }
}
