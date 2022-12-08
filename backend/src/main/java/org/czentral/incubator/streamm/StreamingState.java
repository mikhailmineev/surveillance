/*
 * Copyright 2018 Bence Varga
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

package org.czentral.incubator.streamm;

import org.czentral.util.stream.Processor;

class StreamingState implements Processor {
    
    private static final long ID_CLUSTER = 0x1F43B675;
    private static final long ID_SIMPLEBLOCK = 0xA3;
    private static final long ID_BLOCKGROUP = 0xA0;
    private static final long ID_TIMECODE = 0xE7;
    
    private long clusterTimeCode = 0;
    
    private final Stream stream;
    private final long videoTrackNumber;
    
    private MatroskaFragment fragment;
    
    public StreamingState(Stream stream, long videoTrackNumber) {
        this.stream = stream;
        this.videoTrackNumber = videoTrackNumber;
        fragment = new MatroskaFragment();
    }
    
    @Override
    public int process(byte[] buffer, int offset, int length) {
            
        int endOffset = offset + length;
        int startOffset = offset;
        
        EBMLElement elem;
        
        while (offset < endOffset - 12) {
            elem = new EBMLElement(buffer, offset, length);
            
            // clusters do not need to be fully loaded, so that is an exception
            /* Note: cluster check was moved to be the first because of the
             * possibility of infinite clusters (gstreamer's curlsink?).
             */
            if (elem.getId() != ID_CLUSTER && elem.getEndOffset() > endOffset) {
                
                /* The element is not fully loaded: we need more data, so we end
                 * this processing cycle. The StreamInput will fill the buffer
                 * and take care of the yet unprocessed element. We signal this
                 * by sending the element's offset back. The StreamInput will
                 * keep the data beyound this offset and the next processing cycle
                 * will begin with this element.
                 */
                return elem.getElementOffset() - startOffset;
            }

            /* Timecode for this cluster. We use a flat processing model (we do
             * not care about the nesting of elements), so this timecode will
             * live until we get the next one. It will be the Timecode element
             * of the next Cluster (according to standard).
             */
            if (elem.getId() == ID_TIMECODE) {
                
                // we have the timecode, so open a new cluster in our movie fragment
                clusterTimeCode = EBMLElement.loadUnsigned(buffer, elem.getDataOffset(), (int)elem.getDataSize());
                //System.out.println("tc: " + clusterTimeCode);
                
                // cluster opened
                fragment.openCluster(clusterTimeCode);
                
            } else if (elem.getId() == ID_SIMPLEBLOCK) {
                
                // sample (video or audio) recieved
                
                int trackNum = buffer[elem.getDataOffset()] & 0xff;
                if ((trackNum & 0x80) == 0)
                    throw new RuntimeException("Track numbers > 127 are not implemented.");
                trackNum ^= 0x80;
                
                //DEBUG System.out.print(trackNum + " ");

                // the offset of a video keyframe or -1
                int videoKeyOffset = -1;
                
                // check if this is a video frame
                if (trackNum == videoTrackNumber) {
                    //DEBUG System.out.print("video ");
                    
                    int flags = buffer[elem.getDataOffset() + 3] & 0xff;
                    if ((flags & 0x80) != 0) {
                        // keyframe
                        
                        //DEBUG System.out.print("key ");
                        if (fragment.length() >= MovieFragment.LIMIT_FRAME_MINIMUM) {
                            
                            // closing current cluster (of the curent fragment)
                            fragment.closeCluster();
                            
                            // send the complete fragment to the stream coordinator
                            stream.pushFragment(fragment);
                            
                            // create a new fragment
                            fragment = new MatroskaFragment();
                            
                            // set up new fragment's timecode
                            fragment.openCluster(clusterTimeCode);

                            if ((flags & 0x60) == 0) {
                                // no lacing
                                videoKeyOffset = elem.getDataOffset() + 4;
                            } else {
                                throw new RuntimeException("Lacing is not yet supported.");
                            }
                        }
                    }
                }

                // saving the block
                //fragment.appendBlock(buffer, elem.getElementOffset(), elem.getElementSize());
                fragment.appendKeyBlock(buffer, elem.getElementOffset(), elem.getElementSize(), videoKeyOffset);
                
                //DEBUG System.out.println();
                
                // end: ID_SIMPLEBLOCK
            
            } else if (elem.getId() == ID_BLOCKGROUP) {
                
                // append the BlockGroup to the current fragment
                fragment.appendBlock(buffer, elem.getElementOffset(), elem.getElementSize());

                // BlockGroup element is not supported
                //throw new RuntimeException("BlockGroup is not yet supported.");
                
            
            } else {
                
                // report unhandled element
                //DEBUG System.out.println(elem);
                
            }
            
            if (elem.getId() == ID_CLUSTER || elem.getDataSize() >= 0x100000000L) {
                offset = elem.getDataOffset();
            } else {
                offset = elem.getEndOffset();
            }
        }
        
        return offset - startOffset;
    }

    @Override
    public boolean finished() {
        return false;
    }
    
}
