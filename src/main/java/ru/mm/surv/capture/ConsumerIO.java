package ru.mm.surv.capture;

import java.io.IOException;

@FunctionalInterface
public interface ConsumerIO<T> {

    void accept(T t) throws IOException;

}

