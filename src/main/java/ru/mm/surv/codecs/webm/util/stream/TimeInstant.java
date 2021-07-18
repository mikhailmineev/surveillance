/*
 * Copyright 2019 Bence Varga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.mm.surv.codecs.webm.util.stream;

public class TimeInstant {
    private long ticksPerSecond;
    private long ticks;

    public TimeInstant(long ticksPerSecond, long ticks) {
        this.ticksPerSecond = ticksPerSecond;
        this.ticks = ticks;
    }

    public long getTicksPerSecond() {
        return ticksPerSecond;
    }

    public long getTicks() {
        return ticks;
    }

    public long getMillis() {
        return Math.round(1000 * (double)ticks / ticksPerSecond);
    }

    public TimeInstant transposed(long ticksDelta) {
        return new TimeInstant(ticksPerSecond, ticks + ticksDelta);
    }
}