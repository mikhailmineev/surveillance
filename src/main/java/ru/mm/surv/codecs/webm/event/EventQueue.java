package ru.mm.surv.codecs.webm.event;

/**
 * Event queue for asynchronous processing.
 */
public class EventQueue {

    private final int BUFFER_SIZE = 1024;

    private final Event[] buffer = new Event[BUFFER_SIZE];
    private int bufferOffset = 0;
    private int bufferLength = 0;

    /**
     * Tries to enqueue this event to be processed later by an other thread.
     *
     * @return <code>True</code> if the event has been successfully queued.
     */
    public synchronized boolean post(Event event) {

        // queue full, the event canot be queued.
        if (bufferLength >= BUFFER_SIZE)
            return false;

        // effective offset in the circular buffer
        int offset = (bufferOffset + bufferLength) % BUFFER_SIZE;

        // save the event and increment length
        buffer[offset] = event;
        bufferLength++;

        // wakes up any threads waiting for events to become available
        notify();

        return true;
    }

    /**
     * Un-shifts the event first in the queue. This method <b>blocks</b> until an event
     * is available.
     *
     * @return The event which is in the queue for the longest time (not necessarily the oldest event).
     */
    public Event poll() {

        Event event;

        while ((event = unshift()) == null) {

            // block until events are available
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Event polling thread interrupted.", e);
            }
        }

        return event;

    }

    /**
     * Unshifts the event first in the queue.
     *
     * @return The event which is in the queue for the longest time or <code>null</code> if the queue is empty.
     */
    private synchronized Event unshift() {

        if (bufferLength == 0)
            return null;

        Event event = buffer[bufferOffset];

        // new offset
        bufferOffset = (bufferOffset + 1) % BUFFER_SIZE;

        // decrement length
        bufferLength--;

        return event;
    }
}
