package ru.mm.surv.codecs.webm.incubator.streamm;

import ru.mm.surv.codecs.webm.util.stream.Buffer;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public interface MovieFragment {

    int LIMIT_FRAME_MINIMUM = 100 * 1024;

    int LIMIT_FRAME_MAXIMUM = 2048 * 1024 + 64 * 1024;

    Buffer[] getBuffers();

    int length();

}
