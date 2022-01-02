package ru.mm.surv.codecs.webm.incubator.streamm;

class EBMLElement {

    private final long id;
    private final long size;
    private final byte[] buffer;
    private final int offset;
    private final int dataOffset;


    protected EBMLElement(byte[] buffer, int offset, int length) {

        if (length < 2) {
            throw new RuntimeException("Partial header (buffered sample too small).");
        }

        this.buffer = buffer;
        this.offset = offset;

        int limit = offset + length;

        long sizeFlag;
        long num;

        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            if (offset >= limit) {
                throw new RuntimeException("Partial header (buffered sample too small).");
            }
            num <<= 8;
            sizeFlag <<= 7;
        }

        id = num;

        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            if (offset >= limit) {
                throw new RuntimeException("Partial header (buffered sample too small).");
            }
            num <<= 8;
            sizeFlag <<= 7;
        }

        size = num ^ sizeFlag;

        dataOffset = offset;
    }

    public static long loadUnsigned(byte[] buffer, int offset, int length) {
        long num = 0;
        while (length > 0) {
            length--;
            num <<=8;
            num |= buffer[offset++] & 0xff;
        }

        return num;
    }

    public long getId() {
        return id;
    }

    public long getDataSize() {
        return size;
    }

    public int getElementSize() {
        if (size == 0x1ffffffffffffffL)
            return -1;

        if (size >= 0x100000000L)
            throw new RuntimeException("Element too long to get array offset.");

        return (int)(dataOffset - offset + size);
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getElementOffset() {
        return offset;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public int getEndOffset() {
        if (size == 0x1ffffffffffffffL)
            return -1;

        if ((dataOffset + size) >= 0x100000000L)
            throw new RuntimeException("Element too long to get array offset.");

        return (int)(dataOffset + size);
    }

    public String toString() {
        return "EBMLElement ID:0x" + Long.toHexString(id) + " size: " + size;
    }
}
