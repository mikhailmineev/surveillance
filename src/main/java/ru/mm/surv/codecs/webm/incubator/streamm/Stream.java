package ru.mm.surv.codecs.webm.incubator.streamm;

import ru.mm.surv.codecs.webm.event.EventSourceImpl;

public class Stream extends EventSourceImpl {

    private MovieFragment fragment;
    private int fragmentAge;

    private byte[] header;

    private String mimeType = "application/octet-stream";

    private boolean runs = true;

    public synchronized boolean isRunning() {
        return runs;
    }

    public synchronized void stop() {
        runs = false;
    }

    public synchronized int getFragmentAge() {
        return fragmentAge;
    }

    public synchronized MovieFragment getFragment() {
        return fragment;
    }

    public synchronized byte[] getHeader() {
        return header;
    }

    public synchronized void setHeader(byte[] newHeader) {
        header = newHeader;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public synchronized void pushFragment(MovieFragment newFragment) {
        if (fragmentAge == 0)
            postEvent(new ServerEvent(ServerEvent.INPUT_FIRST_FRAGMENT));
        fragment = newFragment;
        fragmentAge++;
    }
}
