/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.johni0702.minecraft.gui.utils.lwjgl;

import java.nio.ByteBuffer;

/**
 * Readonly interface for Colors
 * @author $Author$
 * @version $Revision$
 * $Id$
 */
public interface WritableColor {

    /**
     * Return the red component (0..255)
     * @return int
     */
    int getRed();

    /**
     * Return the red component (0..255)
     * @return int
     */
    int getGreen();

    /**
     * Return the red component (0..255)
     * @return int
     */
    int getBlue();

    /**
     * Return the red component (0..255)
     * @return int
     */
    int getAlpha();

    /**
     * Return the red component
     * @return int
     */
    byte getRedByte();

    /**
     * Return the red component
     * @return int
     */
    byte getGreenByte();

    /**
     * Return the red component
     * @return int
     */
    byte getBlueByte();

    /**
     * Return the red component
     * @return int
     */
    byte getAlphaByte();

    /**
     * Write the RGBA color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeRGBA(ByteBuffer dest);

    /**
     * Write the RGB color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeRGB(ByteBuffer dest);

    /**
     * Write the ABGR color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeABGR(ByteBuffer dest);

    /**
     * Write the BGR color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeBGR(ByteBuffer dest);

    /**
     * Write the BGRA color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeBGRA(ByteBuffer dest);

    /**
     * Write the ARGB color directly out to a ByteBuffer
     * @param dest the buffer to write to
     */
    void writeARGB(ByteBuffer dest);


}
